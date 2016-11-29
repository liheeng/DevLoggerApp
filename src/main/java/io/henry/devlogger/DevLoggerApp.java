package io.henry.devlogger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * Created by mac on 16/7/29.
 */
public class DevLoggerApp extends AbstractVerticle implements DevLoggerApiConstants, DevLoggerConstants {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    Logger devLogger = LoggerFactory.getLogger("devlogger");

    private LoggerAppContext appContext;

    private LoggerEngineProxy loggerEngine;

    private final String DEFAULT_LISTEN_PORT = "8080";

    private int getListenPort() {
        String port = appContext.getSysProperties().getProperty(LISTEN_PORT);
        if (port == null) {
            port = DEFAULT_LISTEN_PORT;
        }
        return Integer.valueOf(port);
    }

    private void setUp() {
        Properties sysProperties = System.getProperties();

        this.appContext = new LoggerAppContext(sysProperties, vertx);

        loggerEngine = new RedisLogger();
        loggerEngine.connect(this.appContext);

        this.appContext.setLoggerEngine(loggerEngine);
        loggerEngine.restoreClients();

        //        Binding binding = new Binding();
        //        binding.setVariable("foo", new Integer(2));
        //        GroovyShell shell = new GroovyShell(binding);
        //        String script = "System.out.println(\"This is groovy script.\")";
        //        Object value = shell.evaluate("");

    }

    @Override
    public void start(Future<Void> fut) {

        setUp();

        Router router = Router.router(vertx);
        router.post("/api.do").handler(this::onPostApi);
        router.get("/api.do").handler(this::onGetApi);
        router.get("/webroot/*").handler(this::onStaticPage);
        router.get("/").handler(this::onDefault);

        vertx.createHttpServer().requestHandler(router::accept).listen(getListenPort(), result -> {
            if (result.succeeded()) {
                logger.info("The web service starts at port " + getListenPort());
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void onDefault(RoutingContext routeContext) {
        try {
            routeContext.response()
                    .end("<h1>Hello from my first "
                            + "Vert.x 3 application</h1>"
                            + ":"
                            + new File("").getCanonicalPath());
        } catch (IOException e) {
            logger.error(e);
            sendError(404, routeContext.response());
        }
    }

    private void onGetApi(RoutingContext routeContext) {
        try {
            routeContext.response().end("Do not support!");
        } catch (Exception e) {
            logger.error(e);
            sendError(404, routeContext.response());
        }
    }

    private void onPostApi(RoutingContext routeContext) {
        try {
            HttpServerRequest req = routeContext.request();
            HttpServerRequest httpServerRequest = req.bodyHandler(res -> {
                try {
                    String formstr = new String(res.getBytes(), "UTF-8");
                    formstr = JavascriptUtil.decodeURIComponent(formstr);
                    JsonObject formJson = new JsonObject();

                    // Parse request form json object.
                    HttpContentType contentType = HttpUtils.getContentType(routeContext.request().getHeader
                            (HttpConstants.HTTP_HEADER_CONTENT_TYPE));
                    switch (contentType) {

                        case APPLICATION_X_WWW_FORM_URLENCODED:

                            String[] elements = formstr.split("&");
                            for (String e : elements) {
                                String[] kvPair = e.split("=");
                                formJson.put(kvPair[0], kvPair[1]);
                            }
                            break;

                        case MULTIPART_FROM_DATA:
                            formJson = new JsonObject();
                            break;

                        case APPLICATION_JSON:
                            formJson = new JsonObject(formstr);
                            break;

                        case TEXT_XML:
                            formJson = new JsonObject();
                            break;

                        default:
                            formJson = new JsonObject();
                    }

                    // Get biz data and api.
                    String rawBizData = formJson.getString(API_BIZ_DATA);
                    JsonObject bizDataObj = new JsonObject(rawBizData);
                    String apiName = bizDataObj.getString(API_API_NAME);

                    // Dispatch to concrete handler
                    this.apiDispatcher(ApiName.valueOfApi(apiName),
                            new LoggerSessionContext(appContext, routeContext, bizDataObj));
                } catch (Exception e) {
                    logger.error(e);
                    sendError(404, routeContext.response());
                }
            });

        } catch (Exception e) {
            logger.error(e);
            sendError(404, routeContext.response());
        }
    }

    private void apiDispatcher(ApiName apiName, LoggerSessionContext loggerContext) {
        if (apiName == null) {
            sendError(404, loggerContext.getRouteContext().response());
            return;
        }

        switch (apiName) {
            case LOGGING:
                // Save logs.
                handleLogging(apiName, loggerContext);
                break;

            case QUERY_CLIENTS:
                // Query clients.
                handleQueryClients(apiName, loggerContext);
                break;

            case QUERY_LOGS:
                // Query logs.
                handleQueryLogs(apiName, loggerContext);
                break;

            default:
                // Unknown.
                handleDefault(apiName, loggerContext);
        }
    }

    private void handleQueryClients(ApiName apiName, LoggerSessionContext loggerContext) {
        LoggerResult result = new LoggerResult();
        List<Session> list = appContext.getAllSessions();

        boolean noSessions = (list == null || list.size() == 0);
        try {
            // Assemble biz data json object.
            JsonObject bizdata = new JsonObject();
            bizdata.put(API_API_NAME, apiName.getName());
            bizdata.put(API_RESULT_CODE, noSessions ? ErrorCode.NO_SESSIONS.getName() : ErrorCode.SUCCESS
                    .getName());
            bizdata.put(API_SHOW_MESSAGE, noSessions ? ErrorCode.NO_SESSIONS.getContent() : ErrorCode.SUCCESS
                    .getContent());
            if (!noSessions) {
                JsonArray ja = new JsonArray();
                for (Session s : list) {
                    JsonObject sObj = new JsonObject();
                    sObj.put(API_APP_ID, s.getAppId());
                    sObj.put(API_CLIENT_IP, s.getClientIp());
                    sObj.put(API_START, s.getStartTime().toString());
                    sObj.put(API_LAST_UPDATE, s.getLastUpdated().toString());
                    sObj.put(API_LOG_COUNT, s.getLoggingCount());

                    ja.add(sObj);
                }

                bizdata.put(API_CLIENTS, ja);
            }

            // Send response.
            sendResponse(bizdata, loggerContext);

        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    private void handleDefault(ApiName apiName, LoggerSessionContext loggerContext) {
        try {
            // Assemble biz data json object.
            JsonObject bizdata = new JsonObject();
            bizdata.put(API_API_NAME, apiName.getName());
            bizdata.put(API_RESULT_CODE, ErrorCode.UNKNOWN_REQUEST.getName());
            bizdata.put(API_SHOW_MESSAGE, ErrorCode.UNKNOWN_REQUEST.getContent());

            // Send response.
            sendResponse(bizdata, loggerContext);

        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    private void handleQueryLogs(ApiName apiName, LoggerSessionContext loggerContext) {
        LoggerResult result = loggerEngine.query(loggerContext);

        try {
            // Assemble biz data json object.
            JsonObject bizdata = new JsonObject();
            bizdata.put(API_API_NAME, apiName.getName());
            bizdata.put(API_RESULT_CODE, result.getErrorCode().getName());
            bizdata.put(API_SHOW_MESSAGE, result.getErrorCode().getContent());
            if (result.getObjects() != null && result.getObjects().size() > 0) {
                JsonArray ja = new JsonArray();
                for (Object o : result.getObjects()) {
                    ja.add(o.toString());
                }

                bizdata.put(API_LOGS, ja);
            }

            // Send response.
            sendResponse(bizdata, loggerContext);

        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    private void handleLogging(ApiName apiName, LoggerSessionContext loggerContext) {
        LoggerResult result = loggerEngine.logging(loggerContext);

        try {
            // Assemble biz data json object.
            JsonObject bizdata = new JsonObject();
            bizdata.put(API_API_NAME, apiName.getName());
            bizdata.put(API_RESULT_CODE, result.getErrorCode().getName());
            bizdata.put(API_SHOW_MESSAGE, result.getErrorCode().getContent());

            // Send response.
            sendResponse(bizdata, loggerContext);

        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    private void sendResponse(JsonObject bizdata, LoggerSessionContext loggerContext) throws
            UnsupportedEncodingException {
        JsonObject res = new JsonObject();

        res.put(API_BIZ_DATA, JavascriptUtil.encodeURIComponent(bizdata.toString()));

        res.put(API_VERSION, "1.0.0");
        res.put(API_SIGN_TYPE, "RSA");
        res.put(API_SIGN, "");

        String resStr = res.toString();

        HttpServerResponse response = loggerContext.getRouteContext().response();
        response.putHeader("Content-Type", "application/json; charset=utf-8");
        response.putHeader("X-App-Name", "Mall-Web");
        response.putHeader("X-Response-Length", String.valueOf(resStr.getBytes("UTF-8").length));
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Request-Method", "POST, GET");
        response.putHeader("Transfer-Encoding", "chunked");
        response.putHeader("Content-Length", String.valueOf(resStr.getBytes("UTF-8").length));
        response.write(resStr);
        response.end();
    }

    private void onStaticPage(RoutingContext routeContext) {
        try {
            routeContext.response().sendFile("." + routeContext.request().path());
        } catch (Exception e) {
            logger.error(e);
            sendError(404, routeContext.response());
        }
    }
}
