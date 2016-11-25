package io.henry.devlogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 2016/11/15.
 */
public enum ApiName {
    LOGGING("daqian.devlogger.logging"),
    QUERY_LOGS("daqian.devlogger.query_logs"),
    QUERY_CLIENTS("daqian.devlogger.query_clients");

    private String name;

    private ApiName(String name) {
        this.name = name;
    }

    private static Map<String, ApiName> map = new HashMap<String, ApiName>();

    static {
        map.put(LOGGING.name, LOGGING);
        map.put(QUERY_LOGS.name, QUERY_LOGS);
        map.put(QUERY_CLIENTS.name, QUERY_CLIENTS);
    }

    public static ApiName valueOfApi(String name) {
        if (name == null) {
            return null;
        }

        return map.get(name);
    }

    public String getName() {
        return name;
    }
}
