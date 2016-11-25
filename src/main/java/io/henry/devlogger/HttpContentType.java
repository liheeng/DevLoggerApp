package io.henry.devlogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 2016/11/18.
 */
public enum HttpContentType {
    UNKNOWN("__unkonwn"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FROM_DATA("multipart/form-data"),
    APPLICATION_JSON("application/json"),
    TEXT_XML("text/xml");

    private String name;

    private HttpContentType(String name) {
        this.name = name;
    }

    private static Map<String, HttpContentType> map = new HashMap<String, HttpContentType>();
    static {
        map.put(UNKNOWN.name, UNKNOWN);
        map.put(APPLICATION_X_WWW_FORM_URLENCODED.name, APPLICATION_X_WWW_FORM_URLENCODED);
        map.put(MULTIPART_FROM_DATA.name, MULTIPART_FROM_DATA);
        map.put(APPLICATION_JSON.name, APPLICATION_JSON);
        map.put(TEXT_XML.name, TEXT_XML);
    }

    public static HttpContentType nameOf(String name) {
        HttpContentType type = map.get(name);
        if (type == null) {
            return UNKNOWN;
        }
        return type;
    }

    public String getName() {
        return this.name;
    }
}
