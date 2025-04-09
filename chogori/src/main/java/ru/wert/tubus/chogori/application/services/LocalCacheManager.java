// LocalCacheManager.java
package ru.wert.tubus.chogori.application.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static ru.wert.tubus.winform.statics.WinformStatic.HOME_DIRECTORY;

/**
 * Менеджер локального кэширования данных.
 * Обеспечивает сохранение, загрузку и регулярное обновление данных в формате JSON.
 * Реализован как синглтон.
 */
@Slf4j
public class LocalCacheManager {

    private static final String CACHE_SUBDIR = "cache";
    private static final Path CACHE_DIR = Paths.get(HOME_DIRECTORY, CACHE_SUBDIR);
    private static final Gson gson = new GsonBuilder().create();
    private static final long UPDATE_INTERVAL_MINUTES = 60;
    private static final long INITIAL_DELAY_MINUTES = 0;

    private ScheduledExecutorService scheduler;

    // Экземпляр синглтона
    private static volatile LocalCacheManager instance;

    /**
     * Приватный конструктор для предотвращения создания экземпляров извне
     */
    private LocalCacheManager() {
        // Инициализация при необходимости
    }

    /**
     * Возвращает экземпляр синглтона
     * @return Экземпляр LocalCacheManager
     */
    public static LocalCacheManager getInstance() {
        LocalCacheManager localInstance = instance;
        if (localInstance == null) {
            synchronized (LocalCacheManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new LocalCacheManager();
                }
            }
        }
        return localInstance;
    }


    /**
     * Запускает регулярное обновление кэша с заданным интервалом
     * @param onStartCallback Колбек при начале обновления
     * @param onFinishCallback Колбек при завершении обновления
     */
    public void startScheduledCacheUpdates(Runnable onStartCallback, Runnable onFinishCallback) {
        log.info("Запуск регулярного обновления кэша: первый апдейт через {} минут, затем каждые {} минут",
                INITIAL_DELAY_MINUTES, UPDATE_INTERVAL_MINUTES);

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            t.setName("Cache-Update-Thread");
            return t;
        });

        // Первый запуск через INITIAL_DELAY_MINUTES минут, затем каждый час
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(onStartCallback);
            forceCacheUpdate(onFinishCallback);
        }, INITIAL_DELAY_MINUTES, UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Принудительное обновление кэша
     * @param onFinishCallback Колбек при завершении обновления
     */
    public void forceCacheUpdate(Runnable onFinishCallback) {
        log.info("Запуск принудительного обновления кэша...");

        Thread updateThread = new Thread(() -> {
            try {
                // 1. Загрузка свежих данных с сервера
                BatchResponse freshData = BatchService.loadInitialData();
                log.debug("Данные успешно загружены с сервера");

                // 2. Обновление кэша
                saveToCache("initial_data", freshData);
                log.info("Кэш успешно обновлен");

                // 3. Обновление коллекций в QuickServices и таблиц в UI
                Platform.runLater(() -> {
                    ChogoriServices.initFromBatch(freshData);
                    AppStatic.updateTables(); // Добавленный вызов обновления таблиц
                    if (onFinishCallback != null) {
                        onFinishCallback.run();
                    }
                    log.info("Коллекции в QuickServices и таблицы обновлены");
                });

            } catch (IOException e) {
                log.error("Ошибка при обновлении кэша: {}", e.getMessage());
                Platform.runLater(() -> {
                    if (onFinishCallback != null) {
                        onFinishCallback.run();
                    }
                });
            }
        });

        updateThread.setPriority(Thread.MIN_PRIORITY);
        updateThread.setDaemon(true);
        updateThread.start();
    }

    /**
     * Сохраняет данные в кэш
     * @param key Ключ для сохранения
     * @param data Данные для сохранения
     */
    public void saveToCache(String key, Object data) {
        try {
            if (!Files.exists(CACHE_DIR)) {
                Files.createDirectories(CACHE_DIR);
            }

            Path cacheFile = CACHE_DIR.resolve(key + ".json");
            String json = gson.toJson(data);
            Files.write(cacheFile, json.getBytes(StandardCharsets.UTF_8));
            log.debug("Данные сохранены в кэш: {}", cacheFile);
        } catch (IOException e) {
            log.error("Ошибка сохранения данных в кэш", e);
        }
    }

    /**
     * Загружает данные из кэша
     * @param key Ключ для загрузки
     * @param type Тип возвращаемых данных
     * @return Загруженные данные или null
     */
    public <T> T loadFromCache(String key, Class<T> type) {
        try {
            Path cacheFile = CACHE_DIR.resolve(key + ".json");
            if (Files.exists(cacheFile)) {
                String json = new String(Files.readAllBytes(cacheFile), StandardCharsets.UTF_8);
                T data = gson.fromJson(json, type);
                log.debug("Данные загружены из кэша: {}", cacheFile);
                return data;
            }
        } catch (IOException e) {
            log.error("Ошибка загрузки данных из кэша", e);
        }
        log.debug("Данные не найдены в кэше для ключа: {}", key);
        return null;
    }

    public Path getCacheDir() {
        return CACHE_DIR;
    }
}

