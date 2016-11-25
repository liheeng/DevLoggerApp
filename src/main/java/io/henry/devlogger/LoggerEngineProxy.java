package io.henry.devlogger;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by mac on 2016/11/15.
 */
interface LoggerEngineProxy extends DevLoggerConstants {

    void restoreClients();

    boolean connect(LoggerAppContext appContext);

    void disconnect(LoggerAppContext appContext);

    void register(String clientIp, String appId, LoggerSessionContext loggerContext);

    boolean unregister(String clientIp, String appId, LoggerSessionContext loggerContext);

    LoggerResult logging(LoggerSessionContext loggerContext);

    LoggerResult query(LoggerSessionContext loggerContext);

    String saveSession(Session session) throws JsonProcessingException;

    Session getSession(String sessionKey);
}
