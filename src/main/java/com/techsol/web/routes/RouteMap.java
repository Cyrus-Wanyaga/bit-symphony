package com.techsol.web.routes;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.techsol.web.handlers.HTTPHandler;

public class RouteMap {
    private static Map<String, HTTPHandler> routeMap = new ConcurrentHashMap<>();

    public static Map<String, HTTPHandler> getRouteMap() {
        return routeMap;
    }

    public static void setRouteMap(Map<String, HTTPHandler> routeMap) {
        RouteMap.routeMap = routeMap;
    }

    public static boolean addRoute(String path, HTTPHandler handler) throws IOException {
        if (routeMap.containsKey(path)) {
            return false;
        }
        routeMap.put(path, handler);
        return true;
    }
}
