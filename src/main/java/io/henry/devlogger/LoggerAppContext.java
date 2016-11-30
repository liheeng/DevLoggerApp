package io.henry.devlogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;

import java.util.*;

/**
 * Created by mac on 2016/11/15.
 */
public class LoggerAppContext {

    private final Properties sysProperties;

    private final Vertx vertx;

    private LoggerEngineProxy loggerEngine;

    private Map<String, Session> sessionMap = new HashMap<String, Session>( );

    public LoggerAppContext( Properties sysProperties, Vertx vertx ) {
        this.sysProperties = sysProperties;
        this.vertx = vertx;
    }

    public boolean hasSession( String sessionKey ) {
        return sessionMap.containsKey( sessionKey );
    }

    public Session getSession( String sessionKey ) {
        Session s = sessionMap.get( sessionKey );
        if ( s == null ) {
            s = this.loggerEngine.getSession( sessionKey );
            if (s != null) {
                sessionMap.put(sessionKey, s);
            }
        }
        return s;
    }

    public List<Session> getAllSessions() {
        return new ArrayList(sessionMap.values());
    }

    public String saveSession( Session session ) throws JsonProcessingException {
        sessionMap.put( session.getSessionKey(), session );
        return loggerEngine.saveSession( session );
    }

    public Properties getSysProperties( ) {
        return sysProperties;
    }

    public Vertx getVertx( ) {
        return vertx;
    }

    public void setLoggerEngine( LoggerEngineProxy loggerEngine ) {
        this.loggerEngine = loggerEngine;
    }
}
