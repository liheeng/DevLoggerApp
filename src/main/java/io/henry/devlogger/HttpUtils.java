package io.henry.devlogger;

/**
 * Created by mac on 2016/11/25.
 */
public class HttpUtils {
    public static HttpContentType getContentType(String contentTypeContent) {
        if (contentTypeContent == null) {
            return HttpContentType.UNKNOWN;
        }

        String[] items = contentTypeContent.split(";");
        if (items != null && items.length > 0) {
            return HttpContentType.nameOf(items[0]);
        }

        return HttpContentType.UNKNOWN;
    }
}
