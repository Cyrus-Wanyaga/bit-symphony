package com.techsol.web.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;

public class HelloWorld {
    @HTTPPath(path = "/api/hello-world")
    public void index(HTTPRequest httpRequest, HTTPResponse httpResponse) throws TransformerException, IOException {
        String responseBody = "{\"message\": \"Hello from API\"}";
        Map<String, String> headers = new HashMap<>();
        httpResponse.setStatusCode(200);
        httpResponse.setOk(true);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + " " + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(responseBody.length()));
        httpResponse.setHeaders(headers);
        httpResponse.setBody(responseBody.getBytes());
    }
}
