package com.telcel.notifica.carga.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties properties = new Properties();

    private ConfigManager() {
    }

    public static void load(String archivo) throws IOException {

        try (FileInputStream fis = new FileInputStream(archivo)) {
            properties.load(fis);
        }

    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}