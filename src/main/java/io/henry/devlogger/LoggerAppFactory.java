package io.henry.devlogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

/**
 * Created by mac on 2016/11/15.
 */
public class LoggerAppFactory {

    public static String generateSessionKey(String clientIp, String appId) {
        return "daqian.devlogger." + appId + "." + clientIp + ".client";
    }

    public static String generateLogKey(Session session) {
        return session.getSessionKey() + ".log." + session.getLoggingCount();
    }

    public static String generateLogKey(Session session, long position) {
        return session.getSessionKey() + ".log." + position;
    }

    public static JsonObject toJsonObject(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return new JsonObject(mapper.writeValueAsString(obj));
    }

    public static Object toJavaObject(String jsonStr, Class clazz) throws IOException {
        if (jsonStr == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, clazz);
    }
}
