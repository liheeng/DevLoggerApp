package io.henry.devlogger;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mac on 2016/11/15.
 */
public class Session implements Serializable {

    private String sessionKey;

    private String clientIp;

    private String appId;

    private Date startTime;

    private Date lastUpdated;

    private long loggingCount = 0;

    public Session() {

    }

    public Session(String appId, String clientIp) {
        this.appId = appId;
        this.clientIp = clientIp;
        this.sessionKey = LoggerAppFactory.generateSessionKey( clientIp, appId );
    }

    public String getClientIp( ) {
        return clientIp;
    }

    public void setClientIp( String clientIp ) {
        this.clientIp = clientIp;
    }

    public String getAppId( ) {
        return appId;
    }

    public void setAppId( String appId ) {
        this.appId = appId;
    }

    public Date getStartTime( ) {
        return startTime;
    }

    public void setStartTime( Date startTime ) {
        this.startTime = startTime;
        this.setLastUpdated( this.startTime );
    }

    public void increaseCount( ) {
        this.loggingCount++;
        this.setLastUpdated( new Date( ) );
    }

    public Date getLastUpdated( ) {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated ) {
        this.lastUpdated = lastUpdated;
    }

    public long getLoggingCount( ) {
        return loggingCount;
    }

    public void setLoggingCount( long loggingCount ) {
        this.loggingCount = loggingCount;
    }

    public String getSessionKey( ) {
        return this.sessionKey;
    }
}
