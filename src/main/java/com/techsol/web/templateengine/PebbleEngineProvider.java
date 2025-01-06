package com.techsol.web.templateengine;

import com.techsol.utils.TemplateDevUtils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.FileLoader;

public class PebbleEngineProvider {
    private static final PebbleEngine pebbleEngine;
    private static final boolean DEV_MODE = System.getProperty("dev.mode", "false").equals("true");

    static {
        if (DEV_MODE) {
            // Create a file loader for development
            FileLoader fileLoader = new FileLoader();
            fileLoader.setPrefix("src/main/resources"); // Set this to your resources directory

            // Build the engine with the configured loader
            pebbleEngine = new PebbleEngine.Builder()
                    .loader(fileLoader)
                    .strictVariables(false)
                    .cacheActive(false)
                    .build();
            TemplateDevUtils.startDevMode();
        } else {
            ClasspathLoader classpathLoader = new ClasspathLoader();
            classpathLoader.setPrefix("");

            // pebbleEngine = new PebbleEngine.Builder()
            // .loader(classpathLoader)
            // .strictVariables(false)
            // .build();
            FileLoader fileLoader = new FileLoader();
            fileLoader.setPrefix("src/main/resources");
            pebbleEngine = new PebbleEngine.Builder()
                    .loader(fileLoader)
                    .strictVariables(false)
                    .cacheActive(false)
                    .build();
        }
    }

    public PebbleEngine getPebbleEngine() {
        return pebbleEngine;
    }
}
