package com.techsol.web.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.techsol.web.HTTPRequest;
import com.techsol.web.HTTPResponse;
import com.techsol.web.HTTPStatusCode;

public class StaticFileHandler implements HTTPHandler {

    @Override
    public void handle(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException {
        System.out.println("Request: " + httpRequest.toString());
        httpResponse.setBody("<html><body><h1>Hello World</h1></body></html>".getBytes(StandardCharsets.UTF_8));
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/html");
        responseHeaders.put("Content-Length",  String.valueOf(httpResponse.getBody().length));
        httpResponse.setHeaders(responseHeaders);
        httpResponse.setOk(true);
        httpResponse.setStatusCode(200);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
        httpResponse.send();
    }
    
}
