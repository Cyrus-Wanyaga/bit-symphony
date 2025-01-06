package com.techsol.web.ws;

import java.nio.channels.SocketChannel;

public class HMRWebsocket extends WebsocketHandler {
    public HMRWebsocket(SocketChannel socketChannel, String protocol) {
        super(socketChannel);
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("HMR Message Received");
    }

}
