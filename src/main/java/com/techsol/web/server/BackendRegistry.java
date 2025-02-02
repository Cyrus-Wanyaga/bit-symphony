package com.techsol.web.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackendRegistry {
    private final List<BackendServer> backends;

    public BackendRegistry() {
        this.backends = Collections.synchronizedList(new ArrayList<>());
    }

    public void addBackend(BackendServer backend) {
        synchronized (backends) {
            backends.add(backend);
        }
    }

    public void removeBackend(BackendServer backend) {
        synchronized (backends) {
            backends.remove(backend);
        }
    }

    public List<BackendServer> getBackendsCopy() {
        synchronized (backends) {
            return new ArrayList<>(backends);
        }
    }
}
