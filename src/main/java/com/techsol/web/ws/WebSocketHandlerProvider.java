package com.techsol.web.ws;

import java.nio.channels.SocketChannel;

public class WebSocketHandlerProvider {
    public static WebsocketHandler createWebSocketHandlerForPath(String path, SocketChannel clientChannel, String protocol) {
        switch (path) {
            case "/hmr":
                return new HMRWebsocket(clientChannel, protocol);
            default:
                return new WebsocketHandler(clientChannel);
        }
    }
}
