package com.techsol.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HTTPResponse {
    private int statusCode;
    private String statusLine;
    private Map<String, String> headers;
    private byte[] body;
    private boolean ok;
    private OutputStream outputStream;

    public HTTPResponse(){}

    public HTTPResponse(int statusCode, String statusLine, Map<String, String> headers, byte[] body, boolean ok) {
        this.statusCode = statusCode;
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
        this.ok = ok;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send() throws IOException {
    }
}
