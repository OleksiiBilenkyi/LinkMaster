package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try {
            // Завантаження файлу .properties з ресурсів
            InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties");
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("Не вдалося знайти файл application.properties у ресурсах");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отримання значення за ключем
    public static String getValue(String key) {
        return properties.getProperty(key);
    }
}
