package com.techsol.web.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.techsol.utils.transform.IndentHTML;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;

public class StaticFileHandler implements HTTPHandler {

    @Override
    public void handle(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException {
        System.out.println("Request: " + httpRequest.toString());
        String htmlBody = "<html><body><h1>Hello World</h1></body></html>";
        IndentHTML.setInput(htmlBody);
        String transformedHTMLCode = null;
        try {
            transformedHTMLCode = IndentHTML.transformInput();
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
        httpResponse.setBody(transformedHTMLCode == null ? htmlBody.getBytes(StandardCharsets.UTF_8) : transformedHTMLCode.getBytes(StandardCharsets.UTF_8));
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/html");
        responseHeaders.put("Content-Length",  String.valueOf(httpResponse.getBody().length));
        httpResponse.setHeaders(responseHeaders);
        httpResponse.setOk(true);
        httpResponse.setStatusCode(200);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + " " + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
        httpResponse.send();
    }

}
