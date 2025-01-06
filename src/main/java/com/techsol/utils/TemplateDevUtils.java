package com.techsol.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class TemplateDevUtils {
    private static final Path HTML_DIR = Paths.get("src/main/resources/site/templates-dev");
    private static final Path PEB_DIR = Paths.get("src/main/resources/site/templates");

    public static void startDevMode() {
        if (!HTML_DIR.toFile().exists()) {
            try {
                Files.createDirectories(HTML_DIR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!PEB_DIR.toFile().exists()) {
            try {
                Files.createDirectories(PEB_DIR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        convertAllTemplates();

        // new Thread(() -> {
        // try {
        // // WatchDir watcher = new WatchDir(HTML_DIR);
        // // watcher.processEvents();
        // OptimizedDirWatcher watcher = new OptimizedDirWatcher(HTML_DIR);
        // watcher.processEvents();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }, "Template-Watcher").start();
    }

    private static void convertHtmlToPeb(Path pathToCreate, Path htmlFile) {
        try {
            String fileName = htmlFile.getFileName().toString();
            Path pebFile = pathToCreate.resolve(fileName.replace(".html", ".peb"));

            String content = Files.readString(htmlFile);

            Files.writeString(pebFile, content);

            System.out.println("Converted " + htmlFile + " to " + pebFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDirectories(Path pathToCreate) {
        try {
            if (!Files.exists(pathToCreate, LinkOption.NOFOLLOW_LINKS)) {
                System.out.println("Creating directory : " + pathToCreate.toString());
                Files.createDirectories(pathToCreate);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertAllTemplates() {
        try {
            Files.walk(HTML_DIR)
                    .filter(path -> path.toString().endsWith(".html") || path.toString().endsWith(".css")
                            || path.toString().endsWith(".js"))
                    .forEach(htmlPath -> {
                        Path relativePath = HTML_DIR.relativize(htmlPath);
                        System.out.println("Rel path : " + relativePath.toString());
                        // Path targetPath = HTML_DIR.resolve(relativePath);
                        // System.out.println("Target path : " + targetPath);
                        Path pathToCreate = Paths.get(PEB_DIR.toString(), relativePath.toString());
                        createDirectories(pathToCreate.getParent());
                        convertHtmlToPeb(pathToCreate.getParent(), htmlPath);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class OptimizedDirWatcher {
        private final WatchService watcher;
        private final Map<WatchKey, Path> keys;
        private final ExecutorService executorService;
        private final BlockingQueue<FileEvent> eventQueue;
        private final Path HTML_DIR;
        private static final int QUEUE_SIZE = 1000;
        private static final int THREAD_POOL_SIZE = 4;

        public OptimizedDirWatcher(Path htmlDir) throws IOException {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new ConcurrentHashMap<>();
            this.eventQueue = new LinkedBlockingDeque<>(QUEUE_SIZE);
            this.executorService = Executors.newCachedThreadPool();
            this.HTML_DIR = htmlDir;
            this.registerAll(htmlDir);
        }

        private void registerAll(final Path start) throws IOException {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        private void register(Path dir) throws IOException {
            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            keys.put(key, dir);
        }

        public void processEvents() {
            for (int i = 0; i < THREAD_POOL_SIZE; i++) {
                executorService.submit(this::processEventQueue);
            }

            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    break;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path name = ev.context();

                    FileEvent fileEvent = new FileEvent(kind, dir, name);

                    try {
                        eventQueue.put(fileEvent);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }

        private void processEventQueue() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    FileEvent event = eventQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (event == null) {
                        continue;
                    }

                    // CompletableFuture.runAsync(() -> {
                    try {
                        handleFileEvent(event.kind, event.dir, event.name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    // }, executorService);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleFileEvent(WatchEvent.Kind<?> kind, Path dir, Path name) throws IOException {
            Path child = dir.resolve(name);

            if (!isRelevantFile(child)) {
                return;
            }

            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                if (Files.isDirectory(child)) {
                    registerAll(child);
                } else {
                    convertChild(child);
                }
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                convertChild(child);
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                System.out.println("File deleted: " + child);
            }
        }

        private boolean isRelevantFile(Path path) {
            String fileName = path.toString().toLowerCase();
            return fileName.endsWith(".html") || fileName.endsWith(".css") || fileName.endsWith(".js");
        }

        private void convertChild(Path child) throws IOException {
            Path relativePath = HTML_DIR.relativize(child);
            Path pathToCreate = Paths.get(PEB_DIR.toString(), relativePath.toString());
            convertHtmlToPeb(pathToCreate.getParent(), child);
        }
    }

    private static class FileEvent {
        final WatchEvent.Kind<?> kind;
        final Path dir;
        final Path name;

        FileEvent(WatchEvent.Kind<?> kind, Path dir, Path name) {
            this.kind = kind;
            this.dir = dir;
            this.name = name;
        }

        @Override
        public String toString() {
            return "FileEvent [kind=" + kind + ", dir=" + dir + ", name=" + name + "]";
        }
    }

    private static class WatchDir {

        private final WatchService watcher;
        private final Map<WatchKey, Path> keys;

        public WatchDir(Path dir) throws IOException {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<>();
            registerAll(dir);
        }

        private void registerAll(final Path start) throws IOException {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        private void register(Path dir) throws IOException {
            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            keys.put(key, dir);
        }

        void processEvents() {
            for (;;) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    System.out.format("%s: %s\n", event.kind().name(), child);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child)) {
                                registerAll(child);
                            } else if (child.toString().endsWith(".html") || child.toString().endsWith(".css")
                                    || child.toString().endsWith(".js")) {
                                Path relativePath = HTML_DIR.relativize(child);
                                Path pathToCreate = Paths.get(PEB_DIR.toString(), relativePath.toString());
                                convertHtmlToPeb(pathToCreate.getParent(), child);
                            }
                        } catch (IOException x) {
                            x.printStackTrace(); // Handle the exception appropriately
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY && child.toString().endsWith(".html")
                            || child.toString().endsWith(".css")
                            || child.toString().endsWith(".js")) {
                        Path relativePath = HTML_DIR.relativize(child);
                        Path pathToCreate = Paths.get(PEB_DIR.toString(), relativePath.toString());
                        convertHtmlToPeb(pathToCreate.getParent(), child);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE && child.toString().endsWith(".html")
                            || child.toString().endsWith(".css")
                            || child.toString().endsWith(".js")) {
                        // Handle delete if needed
                        System.out.println("File deleted: " + child);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}
