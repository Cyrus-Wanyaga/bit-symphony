package com.techsol.web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class HTTPResponse {
    private int statusCode;
    private String statusLine;
    private Map<String, String> headers;
    private byte[] body;
    private boolean ok;
    private boolean compressContent;
    private OutputStream outputStream;
    private SocketChannel socketChannel;

    public HTTPResponse() {
    }

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

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public boolean isCompressContent() {
        return compressContent;
    }

    public void setCompressContent(boolean compressContent) {
        this.compressContent = compressContent;
    }

    public void send() throws IOException {
        if (this.isCompressContent() && this.getBody() != null) {
            compressBody();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(statusLine).append("\r\n");
        this.headers.forEach((k, v) -> {
            builder.append(k).append(": ").append(v).append("\r\n");
        });
        builder.append("\r\n");

        System.out.println(builder.toString());

        ByteBuffer headerBuffer = StandardCharsets.UTF_8.encode(builder.toString());

        while (headerBuffer.hasRemaining()) {
            this.socketChannel.write(headerBuffer);
        }

        if (body != null && body.length > 0) {
            System.out.println("Response body content exists");

            ByteBuffer bodyBuffer = ByteBuffer.wrap(body);
            while (bodyBuffer.hasRemaining()) {
                socketChannel.write(bodyBuffer);
            }
        }
    }

    private void compressBody() throws IOException {
        System.out.println("Compressing content");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(this.getBody());
        }

        this.setBody(byteArrayOutputStream.toByteArray());
        System.out.println(this.getHeaders().toString());
        this.getHeaders().remove("Content-Length");
        System.out.println(this.getHeaders().toString());
        this.getHeaders().put("Content-Length", String.valueOf(this.body.length));
        this.getHeaders().put("Content-Encoding", "gzip");
        System.out.println(this.getHeaders().toString());
    }
}
