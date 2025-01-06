package com.techsol.app;

import java.util.HashMap;
import java.util.Map;

import com.techsol.web.handlers.HTTPHandler;
import com.techsol.web.handlers.StaticFileHandler;
import com.techsol.web.http.HTTPStatusCode;
import com.techsol.web.routes.RouteMap;

public class RouteMapper {
    public static void mapDefaultRoutes() {
        Map<String, HTTPHandler> routeMap = RouteMap.getRouteMap();

        if (routeMap != null) {
            routeMap.put("/", (req, resp) -> {
                new StaticFileHandler().handle(req, resp);
            });

            routeMap.put("/api/hello", (req, resp) -> {
                String responseBody = "{\"message\": \"Hello from API\"}";
                Map<String, String> headers = new HashMap<>();
                resp.setStatusCode(200);
                resp.setOk(true);
                resp.setStatusLine("HTTP/1.1 " + resp.getStatusCode() + " " + HTTPStatusCode.fromStatusCode(resp.getStatusCode()).getReasonPhrase());
                headers.put("Content-Type", "application/json");
                headers.put("Content-Length", String.valueOf(responseBody.length()));
                resp.setHeaders(headers);
                resp.setBody(responseBody.getBytes());
                resp.send();
            });
        }

        System.out.println("Mapped routes to handlers");
    }
}
