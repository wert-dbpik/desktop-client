package ru.wert.tubus.chogori.application.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j
public class LocalCacheManager {
    private static final String CACHE_DIR = "tubus_cache";
    private static final Gson gson = new GsonBuilder().create();

    public static void saveToCache(String key, Object data) {
        try {
            Path cacheDir = Paths.get(System.getProperty("user.home"), CACHE_DIR);
            if(!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
            }

            Path cacheFile = cacheDir.resolve(key + ".json");
            String json = gson.toJson(data);
            Files.write(cacheFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to save cache", e);
        }
    }

    public static <T> T loadFromCache(String key, Class<T> type) {
        try {
            Path cacheFile = Paths.get(System.getProperty("user.home"), CACHE_DIR, key + ".json");
            if(Files.exists(cacheFile)) {
                String json = new String(Files.readAllBytes(cacheFile), StandardCharsets.UTF_8);
                return gson.fromJson(json, type);
            }
        } catch (IOException e) {
            log.error("Failed to load cache", e);
        }
        return null;
    }
}

