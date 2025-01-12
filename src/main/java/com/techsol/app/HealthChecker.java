package com.techsol.app;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class HealthChecker {
    private final List<BackendServer> backends;
    private static final int HEALTH_CHECK_INTERVAL = 5000; //5 seconds

    public HealthChecker(List<BackendServer> backends) {
        this.backends = backends;
    }

    public void startHealthChecks() {
        Thread healthCheckThread = new Thread(() -> {
            while(true) {
                checkBackends();
                try {
                    Thread.sleep(HEALTH_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        healthCheckThread.setDaemon(true);
        healthCheckThread.start();
    }

    private void checkBackends() {
        for (BackendServer backend : backends) {
            try (Socket socket = new Socket(backend.getHost(), backend.getPort())) {
                backend.setHealthy(true);
            } catch (IOException e) {
                e.printStackTrace();
                backend.setHealthy(false);
            }
        }
    }
}
