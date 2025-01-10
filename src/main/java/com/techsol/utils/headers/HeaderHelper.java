package com.techsol.utils.headers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;

public class HeaderHelper {
    public static Map<String, String> createHeaders(String contentType, Object content) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        if (content instanceof String) {
            headers.put("Content-Length", String.valueOf(((String) content).length()));
        } else if (content instanceof Byte[]) {
            headers.put("Content-Length", String.valueOf(((Byte[]) content).length));
        } else if (content instanceof byte[]) {
            headers.put("Content-Length", String.valueOf(((byte[]) content).length));
        } else if (content instanceof Integer) {
            headers.put("Content-Length", String.valueOf((Integer) content));
        } else if (content instanceof Long) {
            headers.put("Content-Length", String.valueOf((Long) content));
        } else if (content instanceof Double) {
            headers.put("Content-Length", String.valueOf((Double) content));
        } else if (content instanceof Float) {
            headers.put("Content-Length", String.valueOf((Float) content));
        } else if (content instanceof Boolean) {
            headers.put("Content-Length", String.valueOf((Boolean) content));
        }

        return headers;
    }

    public static HTTPResponse createHtmlResponse(String htmlContent, HTTPResponse httpResponse) {
        Map<String, String> headers = createHeaders("text/html", htmlContent);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("EAT"));
        headers.put("Date", dateFormat.format(new Date()));
        httpResponse.setHeaders(headers);

        httpResponse.setBody(htmlContent.getBytes());
        httpResponse.setStatusCode(200);
        httpResponse.setOk(true);
        httpResponse.setStatusLine(generateStatusLine(200));
        return httpResponse;
    }

    private static String generateStatusLine(int statusCode) {
        return "HTTP/1.1 " + statusCode + " " + HTTPStatusCode.fromStatusCode(statusCode).getReasonPhrase();
    }
}
