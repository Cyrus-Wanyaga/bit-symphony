package com.techsol.app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.techsol.web.server.BackendRegistry;
import com.techsol.web.server.BackendServer;

public class LoadBalancedWebServer extends WebServer {
    // private final List<BackendServer> backends;
    private final Map<String, BackendServer> sessionAffinity;
    private final Map<SocketChannel, BackendServer> connectionToBackend;
    private final AtomicInteger currentBackendIndex;
    private final BackendRegistry backendRegistry;
    // private final HealthChecker healthChecker;

    public LoadBalancedWebServer(int port, BackendRegistry backendRegistry) {
        super(port);
        System.out.println("After super");
        // this.backends = new ArrayList<>();
        this.sessionAffinity = new ConcurrentHashMap<>();
        this.connectionToBackend = new ConcurrentHashMap<>();
        this.currentBackendIndex = new AtomicInteger(0);
        this.backendRegistry = backendRegistry;
        // this.healthChecker = new HealthChecker(backends);
    }

    public void addBackend(String host, int port) {
        BackendServer server = new BackendServer(host, port);
        // backends.add(server);
        backendRegistry.addBackend(server);
        server.start();
    }

    @Override
    protected void handleAccept(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        BackendServer backend = selectBackend(clientChannel);

        if (backend != null && backend.isHealthy()) {
            clientChannel.register(selector, SelectionKey.OP_READ);
            connectionToBackend.put(clientChannel, backend);
            backend.incrementConnections();
        } else {
            sendErrorResponse(clientChannel);
            clientChannel.close();
        }
    }

    @Override
    protected void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        BackendServer backend = connectionToBackend.get(clientChannel);

        if (backend == null || !backend.isHealthy()) {
            key.cancel();
            clientChannel.close();
            return;
        }

        super.handleRead(key);
    }

    private BackendServer selectBackend(SocketChannel clientChannel) {
        List<BackendServer> healthyServers = backendRegistry.getBackendsCopy().stream()
        .filter(BackendServer::isHealthy)
        .toList();
        
        if (healthyServers.isEmpty()) {
            return null;
        }

        try {
            String clientId = clientChannel.getRemoteAddress().toString();

            BackendServer affinityServer = sessionAffinity.get(clientId);
            if (affinityServer != null && affinityServer.isHealthy()) {
                return affinityServer;
            }

            BackendServer selected = healthyServers.stream()
                    .min((s1, s2) -> s1.getActiveConnections() - s2.getActiveConnections())
                    .orElse(null);

            if (selected != null) {
                sessionAffinity.put(clientId, selected);
            }

            return selected;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start() {
        // healthChecker.startHealthChecks();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        backendRegistry.getBackendsCopy().forEach(backend -> backend.getServer().stop());
        // backends.forEach(backend -> backend.getServer().stop());
    }

    private void sendErrorResponse(SocketChannel clientChannel) throws IOException {
        String response = "HTTP/1.1 503 Service Unavailable\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 21\r\n" +
                "\r\n" +
                "No backend available.";
        clientChannel.write(ByteBuffer.wrap(response.getBytes()));
    }
}
