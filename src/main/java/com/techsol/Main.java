package com.techsol;

import java.util.Map;
import java.util.function.BiConsumer;

import com.techsol.app.LoadBalancedWebServer;
import com.techsol.database.DatabaseManager;
import com.techsol.database.dao.ConfigDao;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.routes.RouteRegistry;
import com.techsol.web.server.BackendRegistry;
import com.techsol.web.server.HealthChecker;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.PebbleEngine;

public class Main {

    public static void main(String[] args) {
        // Start of by fetching the configs from the database
        Map<String, String> configs = ConfigDao.getConfig();

        // Check if the configs exist.
        // If they don't exist, the database does not exist most likely.
        // In this case, perform one trial to create the database.
        int i = 0;
        while (configs == null && i < 1) {
            System.out.println("Database does not exist...");
            System.out.println("Creating database using the default schema");
            if (DatabaseManager.initializeDatabase()) {
                configs = ConfigDao.getConfig();
            }
            i++;
        }

        // Check if the configs are null and database creation was attempted at least
        // once.
        // If it was attempted and failed (configs are null), then stop the application.
        if (configs == null && i == 1) {
            System.out.println("Database failed to create");
            System.out.println("Stopping application. Goodbye.");
            return;
        }

        System.out.println("Configs: " + configs.toString());

        PebbleEngine pebbleEngine = new PebbleEngineProvider().getPebbleEngine();
        if (pebbleEngine != null) {
            System.out.println("Initialized Pebble Template Engine");
        }

        System.out.println("Registering application routes");

        Map<String, BiConsumer<HTTPRequest, HTTPResponse>> routes = RouteRegistry.ROUTES;

        if (routes.isEmpty()) {
            System.out.println("No routes found.");
        } else {
            for (String path : routes.keySet()) {
                System.out.println(" -> Mapped [" + path + "]");
            }
        }
        System.out.println("Route registration complete.");

        // Executor executor = Executors.newSingleThreadExecutor();
        // executor.execute(() -> {
        //     WebServer webServer = new WebServer();
        // });
        // WebServer webServer = new WebServer();
        // webServer.start();
        BackendRegistry backendRegistry = new BackendRegistry();
        LoadBalancedWebServer server = new LoadBalancedWebServer(8086, backendRegistry);
        HealthChecker healthChecker = new HealthChecker(backendRegistry);

        server.addBackend("127.0.0.1", 8087);
        server.addBackend("127.0.0.1", 8088);
        server.addBackend("127.0.0.1", 8089);

        healthChecker.startHealthChecks();
        server.start();
    }
}
