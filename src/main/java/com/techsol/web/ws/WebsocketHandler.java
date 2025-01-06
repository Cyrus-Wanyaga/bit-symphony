package com.techsol.web.ws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class WebsocketHandler {
    private final SocketChannel clientChannel;

    public WebsocketHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void onOpen() {
        System.out.println("Websocket connection opened: " + clientChannel);
    }

    public void onMessage(ByteBuffer buffer) {
        System.out.println("Websocket message received");

        byte firstByte = buffer.get(0);
        int payloadLength = buffer.get(1) & 0x7F;
        byte[] payload = new byte[payloadLength];
        buffer.position(2);
        buffer.get(payload);

        String message = new String(payload, StandardCharsets.UTF_8);
        System.out.println("Decoded message: " + message);
    }

    public void onError(Exception e) {
        System.err.println("Websocket error: " + e.getMessage());
    }

    public void onClose() {
        System.out.println("Websocket connection closed: " + clientChannel);
    }

    public void sendMessage(String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(message.getBytes(StandardCharsets.UTF_8)));
            clientChannel.write(buffer);
        } catch (IOException e) {
            onError(e);
        }
    }

    private byte[] encodeWebSocketFrame(byte[] messageBytes) {
        int frameSize = messageBytes.length + 2;
        ByteBuffer frame = ByteBuffer.allocate(frameSize);

        frame.put((byte) 0x81); // FIN + text frame opcode
        frame.put((byte) messageBytes.length);
        frame.put(messageBytes);
        return frame.array();
    }
}
