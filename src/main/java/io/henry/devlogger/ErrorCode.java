package io.henry.devlogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 2016/11/16.
 */
public enum ErrorCode {
    SUCCESS(10000, "10000", "Success!"),
    NO_REDIS_CONNECTION(20001, "20001" , "no redis connection." ),
    EXCEPTION_HAPPENED(20002,  "20002", "exception happened."),
    NO_LOGS_FOUND(20003, "20003", "not found any logs."),
    NO_SESSION_LOGS(20004, "20004", "not found logs of specified session."),
    LOG_POSITION_ERROR(20005, "20005", "given log position is error."),
    UNKNOWN_REQUEST(20006, "20006", "unkown request."),
    NO_SESSIONS(20007, "20007", "no sessions."),
    NO_LOGS_CLEARED(20008, "20008", "no logs cleared.");

    private int id;
    private String name;
    private String content;

    ErrorCode(int id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    private static Map<String, ErrorCode> sNameMap = new HashMap<String, ErrorCode>();

    static {
        for (ErrorCode ec : ErrorCode.class.getEnumConstants()) {
            sNameMap.put(ec.name, ec);
        }
    }

    private static Map<Integer, ErrorCode> sIdMap = new HashMap<Integer, ErrorCode>();

    static {
        for (ErrorCode ec : ErrorCode.class.getEnumConstants()) {
            sIdMap.put(ec.id, ec);
        }
    }

    public static ErrorCode errorCodeOf(String name) {
        return sNameMap.get(name);
    }

    public static ErrorCode errorCodeOf(int id) {
        return sIdMap.get(id);
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
