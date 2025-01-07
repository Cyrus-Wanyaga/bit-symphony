package com.techsol.web.ws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class WebsocketHandler {
    private final SocketChannel clientChannel;
    private final String clientID;

    public WebsocketHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
        this.clientID = UUID.randomUUID().toString();
    }

    public void onOpen() {
        System.out.println("Websocket connection opened: " + clientChannel);
    }

    public void onMessage(ByteBuffer buffer) {
        try {
            // Read the first byte
            byte firstByte = buffer.get();
            boolean fin = (firstByte & 0x80) != 0;
            int opcode = firstByte & 0x0F;

            // Read the second byte (mask and payload length)
            byte secondByte = buffer.get();
            boolean masked = (secondByte & 0x80) != 0;
            int payloadLength = secondByte & 0x7F;

            // Handle extended payload lengths
            if (payloadLength == 126) {
                payloadLength = buffer.getShort() & 0xFFFF;
            } else if (payloadLength == 127) {
                long longLength = buffer.getLong();
                if (longLength > Integer.MAX_VALUE) {
                    throw new RuntimeException("Payload too large");
                }
                payloadLength = (int) longLength;
            }

            // Read masking key if present
            byte[] maskingKey = new byte[4];
            if (masked) {
                buffer.get(maskingKey);
            }

            // Read and unmask payload
            byte[] payload = new byte[payloadLength];
            buffer.get(payload);

            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] = (byte) (payload[i] ^ maskingKey[i % 4]);
                }
            }

            // Handle the message based on opcode
            switch (opcode) {
                case 0x1: // Text frame
                    String message = new String(payload, StandardCharsets.UTF_8);
                    broadcast(message);
                    break;
                case 0x8: // Close frame
                    onClose();
                    break;
                case 0x9: // Ping frame
                    sendPong(payload);
                    break;
                case 0xA: // Pong frame
                    System.out.println("Received pong");
                    break;
                default:
                    System.out.println("Unsupported opcode: " + opcode);
            }
        } catch (Exception e) {
            onError(e);
        }
    }

    private void sendPong(byte[] payload) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(payload, 0xA));
            clientChannel.write(buffer);
        } catch (IOException e) {
            onError(e);
        }
    }

    public void onError(Exception e) {
        System.err.println("Websocket error: " + e.getMessage());
    }

    public void onClose() {
        System.out.println("Websocket connection closed: " + clientChannel);
    }

    public void sendMessage(String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(message.getBytes(StandardCharsets.UTF_8), 0x1));
            clientChannel.write(buffer);
        } catch (IOException e) {
            onError(e);
        }
    }

    private byte[] encodeWebSocketFrame(byte[] payload, int opcode) {
        int length = payload.length;
        ByteBuffer frame;

        if (length <= 125) {
            frame = ByteBuffer.allocate(2 + length);
            frame.put((byte) (0x80 | opcode));
            frame.put((byte) length);
        } else if (length <= 65535) {
            frame = ByteBuffer.allocate(4 + length);
            frame.put((byte) (0x80 | opcode));
            frame.put((byte) 126);
            frame.putShort((short) length);
        } else {
            frame = ByteBuffer.allocate(10 + length);
            frame.put((byte) (0x80 | opcode));
            frame.put((byte) 127);
            frame.putLong(length);
        }

        frame.put(payload);
        return frame.array();
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    public String getClientID() {
        return clientID;
    }

    public void broadcast(String message) {
        System.out.println("Attempting to broadcast");
        for (Map.Entry<SocketChannel, WebsocketHandler> entry : WebSocketHandlers.getWebSocketHandlers().entrySet()) {
            if (entry.getKey() != clientChannel) {
                entry.getValue().sendMessage(message);
            }
        }
    }
}
