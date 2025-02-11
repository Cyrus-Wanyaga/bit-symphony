package com.techsol.web.controller;

import static com.techsol.config.Constants.FILE_SEPARATOR;
import static com.techsol.config.Constants.STATIC_FILES_PATH;
import static com.techsol.config.Constants.TEMPLATES_PATH;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;
import com.techsol.web.http.MimeTypes;

public class DefaultResources {
    @HTTPPath(path = "/**")
    public void handleDefaultResources(HTTPRequest request, HTTPResponse response) {
        String path = request.getUrl();

        if (path.equals("/favicon.ico")) {
            handleFavicon(response);
            return;
        }

        if (path.contains("images")) {
            handleImages(path, response);
            return;
        }

        String resourcePath = TEMPLATES_PATH + path;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                byte[] content = is.readAllBytes();
                String contentType = MimeTypes.getMimeType(path);

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", contentType);
                headers.put("Content-Length", String.valueOf(content.length));

                // Add caching headers for static resources
                // headers.put("Cache-Control", "public, max-age=31536000");

                response.setHeaders(headers);
                response.setBody(content);
                response.setStatusCode(200);
                response.setOk(true);
                response.setStatusLine("HTTP/1.1 200 " + HTTPStatusCode.fromStatusCode(200).getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFavicon(HTTPResponse response) {
        String faviconPath = STATIC_FILES_PATH + "favicon" + FILE_SEPARATOR + "favicon.ico";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(faviconPath)) {
            if (is != null) {
                byte[] content = is.readAllBytes();

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "image/x-icon");
                headers.put("Content-Length", String.valueOf(content.length));
                headers.put("Cache-Control", "public, max-age=31536000");

                response.setHeaders(headers);
                response.setBody(content);
                response.setStatusCode(200);
                response.setOk(true);
                response.setStatusLine("HTTP/1.1 200 " + HTTPStatusCode.fromStatusCode(200).getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleImages(String path, HTTPResponse response) {
        String imagePath = STATIC_FILES_PATH + "images" + String.join(File.separator, path.split("/")).replace("images" + File.separator, "");
        System.out.println("Image path: " + imagePath);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath)) {
            if (is != null) {
                byte[] content = is.readAllBytes();
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "image/*");
                headers.put("Content-Length", String.valueOf(content.length));
                headers.put("Cache-Control", "public, max-age=31536000");

                response.setHeaders(headers);
                response.setBody(content);
                response.setStatusCode(200);
                response.setOk(true);
                response.setStatusLine("HTTP/1.1 200 " + HTTPStatusCode.fromStatusCode(200).getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
