package io.henry.devlogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * Created by mac on 2016/11/15.
 */
public class RedisLogger implements LoggerEngineProxy, DevLoggerApiConstants {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private LoggerAppContext appContext;

    private String DEFAULT_REDIS_HOST = "127.0.0.1";

    private JedisPool jedisPool = null;

    @Override
    public boolean connect(LoggerAppContext appContext) {
        this.appContext = appContext;
        return connectImpl(false);
    }

    @Override
    public void restoreClients() {
        Jedis jedis = jedisPool.getResource();

        try {
            Set<String> keys = jedis.keys(DevLoggerConstants.CLIENT_SESSION_KEY_PATTERN);
            for (String key : keys) {
                appContext.getSession(key);
            }
        } finally {
            jedis.close();
        }
    }

    private boolean connectImpl(boolean reconnect) {
        if (reconnect) {
            this.disconnect(this.appContext);
        } else if (jedisPool != null) {
            return true;
        }

        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxTotal(100);
        jedisPool = new JedisPool(jpc, getRedisHost());
        if (jedisPool == null) {
            logger.error("Failed to connect Redis server with " + getRedisHost());
            return false;
        }

        logger.info("Succeeded to connect Redis server with " + getRedisHost());
        return true;
    }

    private String getRedisHost() {
        String host = appContext.getSysProperties().getProperty("redishost");
        if (host == null) {
            host = DEFAULT_REDIS_HOST;
        }
        return host;
    }

    @Override
    public void disconnect(LoggerAppContext appContext) {
        jedisPool.close();
    }

    @Override
    public void register(String clientIp, String appId, LoggerSessionContext loggerContext) {

    }

    @Override
    public boolean unregister(String clientIp, String appId, LoggerSessionContext loggerContext) {
        return false;
    }

    @Override
    public LoggerResult logging(LoggerSessionContext loggerContext) {
        String appId = loggerContext.getRequestData().getString(API_APP_ID);
        String clientIp = loggerContext.getRequestData().getString(API_CLIENT_IP);
        String log = loggerContext.getRequestData().getString(API_LOG);

        LoggerResult result = new LoggerResult();
        if (connectImpl(false)) {
            try {
                // Get or create session.
                String sessionKey = LoggerAppFactory.generateSessionKey(clientIp, appId);
                Session session = appContext.getSession(sessionKey);
                if (session == null) {
                    // Create new session firstly.
                    session = new Session(appId, clientIp);
                    session.setStartTime(new Date());

                    appContext.saveSession(session);

                }

                // Save log.
                session.increaseCount();
                Jedis jedis = jedisPool.getResource();
                try {
                    jedis.set(LoggerAppFactory.generateLogKey(session), log);
                } finally {
                    jedis.close();
                }

                // Update session.
                appContext.saveSession(session);

                result.setSession(session);

            } catch (JsonProcessingException e) {
                logger.error(e);
                result.setErrorCode(ErrorCode.EXCEPTION_HAPPENED);
                result.setThrowable(e);

            }
        } else {
            result.setErrorCode(ErrorCode.NO_REDIS_CONNECTION);
        }
        return result;
    }

    @Override
    public LoggerResult query(LoggerSessionContext
                                      loggerContext) {
        String appId = loggerContext.getRequestData().getString(API_APP_ID);
        String clientIp = loggerContext.getRequestData().getString(API_CLIENT_IP);
        String startstr = loggerContext.getRequestData().getString(API_START);
        long start = (startstr == null) ? -1L : Long.valueOf(startstr);
        String lengthstr = loggerContext.getRequestData().getString((API_LENGTH));
        long length = (lengthstr == null) ? -1L : Long.valueOf(lengthstr);

        LoggerResult result = new LoggerResult();
        if (start == -1) {
            result.setErrorCode(ErrorCode.LOG_POSITION_ERROR);
            return result;
        }

        // Find session.
        Session session = appContext.getSession(LoggerAppFactory.generateSessionKey(clientIp, appId));
        if (session != null) {
            // Get logs
            long end = (length == -1) ? session.getLoggingCount() : (start + length);
            Jedis jedis = jedisPool.getResource();
            try {
                for (long i = start; i <= end; i++) {
                    String log = jedis.get(LoggerAppFactory.generateLogKey(session, i));
                    if (log != null) {
                        result.addObject(log);
                    }
                }
            } finally {
                jedis.close();
            }

            if (result.getObjects() == null || result.getObjects().size() == 0) {
                result.setErrorCode(ErrorCode.NO_LOGS_FOUND);
            }
        } else {
            result.setErrorCode(ErrorCode.NO_SESSION_LOGS);
        }

        return result;
    }

    @Override
    public String saveSession(Session session) throws JsonProcessingException {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.set(session.getSessionKey(), LoggerAppFactory.toJsonObject(session).toString());
        } finally {
            jedis.close();
        }
    }

    @Override
    public Session getSession(String sessionKey) {
        Jedis jedis = jedisPool.getResource();
        try {
            return (Session) LoggerAppFactory.toJavaObject(jedis.get(sessionKey), Session.class);
        } catch (IOException e) {
            logger.error(e);
        } finally {
            jedis.close();
        }

        return null;
    }
}
