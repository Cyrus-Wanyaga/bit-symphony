package com.techsol.web.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class HealthChecker {
    private final BackendRegistry backendRegistry;

    public HealthChecker(BackendRegistry backendRegistry) {
        this.backendRegistry = backendRegistry;
    }

    public void startHealthChecks() {
        Thread healthCheckThread = new Thread(() -> {
            while (true) {
                List<BackendServer> backends = backendRegistry.getBackendsCopy();
                for (BackendServer backend : backends) {
                    boolean previouslyHealthy = backend.isHealthy();
                    boolean isHealthy = isBackendReachable(backend);

                    backend.setHealthy(isHealthy);

                    if (isHealthy && !previouslyHealthy) {
                        System.out.println("Backend is now healthy: " + backend.getHost() + ":" + backend.getPort());
                    } else if (!isHealthy && previouslyHealthy) {
                        System.out.println("Backend is now unhealthy: " + backend.getHost() + ":" + backend.getPort());
                    }
                }
                try {
                    Thread.sleep(5000);
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

    private boolean isBackendReachable(BackendServer backend) {
        try (Socket socket = new Socket(backend.getHost(), backend.getPort())) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // private void checkBackends() {
    // for (BackendServer backend : backends) {
    // try (Socket socket = new Socket(backend.getHost(), backend.getPort())) {
    // backend.setHealthy(true);
    // } catch (IOException e) {
    // e.printStackTrace();
    // backend.setHealthy(false);
    // }
    // }
    // }
}
