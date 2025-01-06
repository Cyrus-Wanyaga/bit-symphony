package com.techsol.web.http;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put(".aac", "audio/aac");
        mimeTypes.put(".abw", "application/x-abiword");
        mimeTypes.put("apng", "image/apng");
        mimeTypes.put(".arc", "application/x-freearc");
        mimeTypes.put(".avif", "image/avif");
        mimeTypes.put(".azw", "application/vnd.amazon.ebook");
        mimeTypes.put(".bin", "application/octet-stream");
        mimeTypes.put(".bmp", "image/bmp");
        mimeTypes.put(".bz", "application/x-bzip");
        mimeTypes.put(".bz2", "application/x-bzip2");
        mimeTypes.put(".cda", "application/x-cdf");
        mimeTypes.put(".csh", "application/x-csh");
        mimeTypes.put(".css", "text/css");
        mimeTypes.put(".csv", "text/csv");
        mimeTypes.put(".doc", "application/msword");
        mimeTypes.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeTypes.put(".eot", "application/vnd.ms-fontobject");
        mimeTypes.put(".epub", "application/epub+zip");
        mimeTypes.put(".gz", "application/gzip");
        mimeTypes.put(".gif", "image/gif");
        mimeTypes.put(".htm", "text/html");
        mimeTypes.put(".html", "text/html");
        mimeTypes.put(".ico", "image/vnd.microsoft.icon");
        mimeTypes.put(".ics", "text/calendar");
        mimeTypes.put(".jar", "application/java-archive");
        mimeTypes.put(".jpeg", "image/jpeg");
        mimeTypes.put(".jpg", "image/jpeg");
        mimeTypes.put(".js", "text/javascript");
        mimeTypes.put(".json", "application/json");
        mimeTypes.put(".jsonld", "application/ld+json");
        mimeTypes.put(".otf", "font/otf");
        mimeTypes.put(".png", "image/png");
        mimeTypes.put(".pdf", "application/pdf");
        mimeTypes.put(".php", "application/x-httpd-php");
        mimeTypes.put(".xml", "application/xml||text/xml");
    }

    public static String getMimeType(String path) {
        return mimeTypes.entrySet().stream().filter(entry -> path.endsWith(entry.getKey())).map(Map.Entry::getValue)
                .findFirst().orElse("application/octet-stream");
    }
}
