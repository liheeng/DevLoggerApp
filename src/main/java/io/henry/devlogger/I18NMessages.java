package io.henry.devlogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mac on 2016/11/16.
 */
public class I18NMessages {

    private static Locale locale = Locale.getDefault();

    private static Map<Locale, Properties> msgMap = new HashMap<Locale, Properties>();

    public static void setLocale(Locale locale) {
        I18NMessages.locale = locale;
    }

    public static String getMessage(String key) {
        return getMessage(key, locale);
    }

    public static String getMessage(String key, Locale locale) {
        if (!msgMap.containsKey(locale)) {
            Properties p = new Properties();
            URL url = I18NMessages.class.getResource("/messages.properties." + locale.getLanguage() + "." + locale.getCountry());
            if (url != null) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(url.getPath()),
                            "UTF-8"));
                    String s;
                    while ((s = br.readLine()) != null) {
                        try {
                            String[] ss = s.split("=");
                            p.put(ss[0], ss[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    msgMap.put(locale, p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Properties p = msgMap.get(locale);
        if (p == null) {
            return "[" + key + "]";
        }
        String value = p.getProperty(key);
        if (value == null) {
            return "[" + key + "]";
        } else {
            return value;
        }
    }
}
