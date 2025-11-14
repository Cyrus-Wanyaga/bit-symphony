package com.techsol.utils.directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryManager {

    public static DirectoryCleanupResult manageOutputDirectory(Path directory, long sizeThresholdBytes)
            throws IOException {
        int maxFileIndex = -1;
        long totalSize = 0;

        final Pattern filePattern = Pattern.compile("Write_(chunked|full)_(\\d+)\\.txt");

        List<Path> filesInDir = new ArrayList<>();

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            return new DirectoryCleanupResult(0, 0L);
        }

        try (Stream<Path> stream = Files.list(directory)) {
            List<Path> paths = stream.collect(Collectors.toList());
            if (paths.isEmpty()) {
                return new DirectoryCleanupResult(0, 0L);
            }

            for (Path path : paths) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                filesInDir.add(path);
                totalSize += Files.size(path);

                String fileName = path.getFileName().toString();
                Matcher matcher = filePattern.matcher(fileName);
                if (matcher.matches()) {
                    try {
                        int fileIndex = Integer.parseInt(matcher.group(2));
                        if (fileIndex > maxFileIndex) {
                            maxFileIndex = fileIndex;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Could not parse index from filename: " + fileName);
                    }
                }
            }
        }

        if (totalSize > sizeThresholdBytes) {
            System.out.println("Directory size (" + totalSize + " bytes) exceeds threshold (" + sizeThresholdBytes
                    + " bytes). Deleting all files.");

            for (Path fileToDelete : filesInDir) {
                try {
                    Files.delete(fileToDelete);
                } catch (IOException e) {
                    System.err.println("Failed to delete file: " + fileToDelete + "-" + e.getMessage());
                }
            }

            return new DirectoryCleanupResult(0, 0L);
        } else {
            int nextIndex = maxFileIndex + 1;
            System.out.println("Directory size (" + totalSize + " bytes) is within threshold. Starting from file index "
                    + nextIndex);
            return new DirectoryCleanupResult(nextIndex, totalSize);
        }
    }

    public static class DirectoryCleanupResult {
        final int nextFileIndex;
        final long directorySize;

        public DirectoryCleanupResult(int nextFileIndex, long directorySize) {
            this.nextFileIndex = nextFileIndex;
            this.directorySize = directorySize;
        }

        public int getNextFileIndex() {
            return nextFileIndex;
        }

        public long getDirectorySize() {
            return directorySize;
        }

    }
}
