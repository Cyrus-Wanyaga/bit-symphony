
/**
 * @author Cyrus Wanyaga
 * @version 1.0
 */

package com.techsol.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a http 1.1 request.
 * 
 * A http request has the following properties:
 * <ul>
 * <li>Request Line</li>
 * <li>Headers</li>
 * <li>Body</li>
 * </ul>
 * 
 */
public class HTTPRequest {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private int contentLength;
    private byte[] body;

    public HTTPRequest(String method, String url) {
        this.method = method;
        this.url = url;
        this.headers = new HashMap<>();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void addHeader(String name, String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, value);
        }
    }

    @Override
    public String toString() {
        return "HTTPRequest [method=" + method + ", url=" + url + ", contentLength=" + contentLength + "]";
    }
}
