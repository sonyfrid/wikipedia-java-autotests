package io.github.sonyfrid.wiki.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Настройки из config.properties; системные свойства (-Dkey=value) важнее файла. */
public final class Config {
    private static final Properties FILE = load();

    private Config() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать config.properties", e);
        }
        return p;
    }

    public static String get(String key) {
        String value = System.getProperty(key, FILE.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Нет настройки " + key);
        }
        return value;
    }

    public static String lang() {
        return get("wiki.lang");
    }

    public static String userAgent() {
        return get("wiki.userAgent");
    }

    public static String baseUrl(String lang) {
        return "https://" + lang + ".wikipedia.org";
    }

    public static String mobileUrl(String lang) {
        return "https://" + lang + ".m.wikipedia.org";
    }
}
