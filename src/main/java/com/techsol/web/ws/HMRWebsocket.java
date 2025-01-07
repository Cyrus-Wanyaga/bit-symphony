package com.techsol.web.ws;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HMRWebsocket extends WebsocketHandler {
    public HMRWebsocket(SocketChannel socketChannel, String protocol) {
        super(socketChannel);
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);
    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        System.out.println("Before websocket message received");
        super.onMessage(buffer);

        System.out.println("HMR Message Received");
    }
}
