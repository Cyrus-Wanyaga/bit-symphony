package com.techsol.app;

import java.util.concurrent.atomic.AtomicInteger;

public class BackendServer {
    private final String host;
    private final int port;
    private volatile boolean healthy = true;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final WebServer server;

    public BackendServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.server = new WebServer(port);
    }

    public void start() {
        System.out.println("Received req to start server");
        Thread serverThread = new Thread(() -> {
            server.start();
        });
        serverThread.setDaemon(true);  // Make it a daemon thread so it doesn't prevent JVM shutdown
        serverThread.start();

    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.decrementAndGet();
    }

    public int getActiveConnections() {
        return activeConnections.get();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public WebServer getServer() {
        return server;
    }
}
