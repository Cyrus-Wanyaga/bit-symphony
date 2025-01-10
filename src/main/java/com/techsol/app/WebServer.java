package com.techsol.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.routes.RouteRegistry;
import com.techsol.web.ws.WebSocketHandlerProvider;
import com.techsol.web.ws.WebSocketHandlers;
import com.techsol.web.ws.WebsocketHandler;

public class WebServer {
    private static final int PORT = 8086;
    private static final ExecutorService threadPool = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public WebServer() {
        try (Selector selector = Selector.open();
                ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port " + PORT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        handleRequest(clientChannel, key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles incoming HTTP request from the given client channel.
     *
     * @param client the SocketChannel associated with the client
     * @param key    the SelectionKey associated with the client
     * @throws IOException if an I/O error occurs during request handling
     */
    private static void handleRequest(SocketChannel client, SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        StringBuilder requestBuilder = new StringBuilder();

        try {
            int bytesRead;
            while ((bytesRead = client.read(buffer)) > 0) {
                buffer.flip();
                requestBuilder.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
            }

            if (bytesRead == -1) {
                key.cancel();
                client.close();
                return;
            }

            String requestString = requestBuilder.toString();
            if (requestString.isEmpty()) {
                return;
            }

            if (isWebSocketHandshake(requestString)) {
                HTTPRequest request = parseRequest(requestString);
                handleWebSocketHandshake(client, request);
            } else if (WebSocketHandlers.getWebSocketHandlers().containsKey(client)) {
                handleWebSocketRequest(client, buffer);
            } else {
                key.cancel();

                threadPool.submit(() -> {
                    try {
                        HTTPRequest request = parseRequest(requestString);
                        HTTPResponse response = new HTTPResponse();
                        response.setSocketChannel(client);
                        System.out.println("Request: " + request.toString());

                        BiConsumer<HTTPRequest, HTTPResponse> handler = RouteRegistry.ROUTES.get(request.getUrl());
                        if (handler != null) {
                            handler.accept(request, response);
                        } else {
                            handler = RouteRegistry.ROUTES.get("/**");
                            if (handler != null) {
                                handler.accept(request, response);
                            }
                        }

                        synchronized (client) {
                            if (client.isOpen()) {
                                response.send();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.err.println("Client connection reset: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
            client.close();
        }
    }

    /**
     * Parses a HTTP request string into an instance of {@link HTTPRequest}.
     * 
     * @param requestString the HTTP request string
     * @return the parsed HTTP request
     * @throws MalformedURLException if the request line is malformed
     */
    private static HTTPRequest parseRequest(String requestString) throws MalformedURLException {
        String[] lines = requestString.split("\r\n");
        String[] requestLineParts = lines[0].split(" ");

        HTTPRequest request = new HTTPRequest(requestLineParts[0], requestLineParts[1]);

        int headerEnd = 1;
        for (; headerEnd < lines.length; headerEnd++) {
            if (lines[headerEnd].isEmpty())
                break;
            String[] headerParts = lines[headerEnd].split(": ");
            request.getHeaders().put(headerParts[0], headerParts[1]);
        }

        if (headerEnd + 1 < lines.length) {
            request.setBody(String.join("\r\n", Arrays.copyOfRange(lines, headerEnd + 1, lines.length))
                    .getBytes(StandardCharsets.UTF_8));
        }

        return request;
    }

    /**
     * Checks if the given request string is a WebSocket handshake request.
     *
     * @param request the HTTP request string
     * @return true if the request is a WebSocket handshake request, false otherwise
     */
    private static boolean isWebSocketHandshake(String request) {
        return request.contains("Connection: Upgrade") && request.contains("Upgrade: websocket");
    }

    /**
     * Handles a WebSocket handshake request.
     *
     * @param clientChannel the SocketChannel object that is used to send a response
     *                      to the client
     * @param request       the HTTP request object containing the request headers
     *                      and WebSocket protocol
     * @throws IOException if an I/O error occurs during response write
     */
    private static void handleWebSocketHandshake(SocketChannel clientChannel, HTTPRequest request) throws IOException {
        String webSocketKey = request.getHeaders().get("Sec-WebSocket-Key");
        String acceptKey = generateWebSocketAcceptKey(webSocketKey);

        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Protocol: " + request.getHeaders().get("Sec-WebSocket-Protocol") + "\r\n" +
                "Access-Control-Allow-Origin: * \r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";

        clientChannel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
        WebsocketHandler handler = WebSocketHandlerProvider.createWebSocketHandlerForPath(request.getUrl(),
                clientChannel, request.getHeaders().get("Sec-WebSocket-Protocol"));
        if (handler != null) {
            WebSocketHandlers.addWebSocketHandler(clientChannel, handler);
            handler.onOpen();
            System.out.println("Websocket handshake complete: " + clientChannel.getRemoteAddress());
        } else {
            clientChannel.close();
            System.out.println("WebSocket path not found");
        }
    }

    /**
     * Generates the Sec-WebSocket-Accept key required for the WebSocket handshake
     * by
     * taking the given key and concatenating it with the magic string
     * "258EAFA5-E914-47DA-95CA-C5AB0DC85B11",
     * computing the SHA-1 hash of the resulting string, and then base64-encoding
     * it.
     *
     * @param key the client's Sec-WebSocket-Key header
     * @return the base64-encoded SHA-1 hash of the given key concatenated with the
     *         magic string
     * @throws RuntimeException if there is an error generating the hash
     */
    private static String generateWebSocketAcceptKey(String key) {
        try {
            String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(acceptSeed.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Sec-WebSocket-Accept key", e);
        }
    }

    /**
     * Handles incoming WebSocket requests by delegating the received message
     * to the appropriate WebsocketHandler instance associated with the given
     * client channel.
     *
     * @param clientChannel the SocketChannel associated with the WebSocket client
     * @param buffer        the ByteBuffer containing the incoming WebSocket message
     */
    private static void handleWebSocketRequest(SocketChannel clientChannel, ByteBuffer buffer) {
        WebsocketHandler handler = WebSocketHandlers.getWebSocketHandlers().get(clientChannel);
        if (handler != null) {
            System.out.println(handler.getClass().getName());
            handler.onMessage(buffer);
        }
    }
}
