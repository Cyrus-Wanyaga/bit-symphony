package com.techsol.web.ws;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandlers {
    private static Map<SocketChannel, WebsocketHandler> webSocketHandlers = new ConcurrentHashMap<>();

    public static Map<SocketChannel, WebsocketHandler> getWebSocketHandlers() {
        return webSocketHandlers;
    }

    public static void addWebSocketHandler(SocketChannel clientChannel, WebsocketHandler handler) {
        webSocketHandlers.put(clientChannel, handler);
    }   

    public static void removeWebSocketHandler(SocketChannel clientChannel) {
        webSocketHandlers.remove(clientChannel);
    }
}
