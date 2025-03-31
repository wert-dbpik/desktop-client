package ru.wert.tubus.chogori.application.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.wert.tubus.winform.statics.WinformStatic.HOME_DIRECTORY;

/**
 * Менеджер локального кэширования данных.
 * Обеспечивает сохранение и загрузку данных в формате JSON.
 * Кэш хранится в директории приложения Tubus.
 */
@Slf4j
public class LocalCacheManager {

    // Имя поддиректории для хранения кэша
    private static final String CACHE_SUBDIR = "cache";

    // Полный путь к директории кэша: {HOME_DIRECTORY}/cache/
    private static final Path CACHE_DIR = Paths.get(HOME_DIRECTORY, CACHE_SUBDIR);

    // Экземпляр Gson для сериализации/десериализации
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Сохраняет данные в кэш.
     *
     * @param key ключ для идентификации данных
     * @param data объект для сохранения (будет сериализован в JSON)
     */
    public static void saveToCache(String key, Object data) {
        try {
            // Создаем директорию кэша, если она не существует
            if (!Files.exists(CACHE_DIR)) {
                Files.createDirectories(CACHE_DIR);
            }

            // Формируем путь к файлу кэша
            Path cacheFile = CACHE_DIR.resolve(key + ".json");

            // Сериализуем объект в JSON
            String json = gson.toJson(data);

            // Записываем данные в файл
            Files.write(cacheFile, json.getBytes(StandardCharsets.UTF_8));

            log.debug("Данные сохранены в кэш: {}", cacheFile);
        } catch (IOException e) {
            log.error("Ошибка сохранения данных в кэш", e);
        }
    }

    /**
     * Загружает данные из кэша.
     *
     * @param key ключ идентификации данных
     * @param type класс типа возвращаемых данных
     * @return загруженный объект или null, если данные не найдены
     */
    public static <T> T loadFromCache(String key, Class<T> type) {
        try {
            // Формируем путь к файлу кэша
            Path cacheFile = CACHE_DIR.resolve(key + ".json");

            // Проверяем существование файла
            if (Files.exists(cacheFile)) {
                // Читаем содержимое файла
                String json = new String(Files.readAllBytes(cacheFile), StandardCharsets.UTF_8);

                // Десериализуем JSON в объект
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
}

