package com.techsol.tests.file;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.techsol.tests.PerformanceResult;
import com.techsol.tests.PerformanceTest;
import com.techsol.tests.TestResourceMonitor;

public class FileReaderTest implements PerformanceTest {
    private final Path inputFile;
    private final boolean buffered;
    private final int bufferSize;

    public FileReaderTest(Path inputFile, boolean buffered, int bufferSize) {
        this.inputFile = inputFile;
        this.buffered = buffered;
        this.bufferSize = bufferSize;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return Map.of(
            "inputFile", inputFile.toString(),
            "buffered", buffered,
            "bufferSize", bufferSize
        );
    }

    @Override
    public String getName() {
        return "File Read Test (" + (buffered ? "buffered" : "unbuffered") + ")";
    }

    @Override
    public PerformanceResult runTest(TestResourceMonitor monitor) throws Exception {
        monitor.start();
        long start = System.currentTimeMillis();

        int totalLines = 0;
        if (buffered) {
            try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
                List<String> buffer = new ArrayList<>(bufferSize);
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.add(line);
                    if (buffer.size() >= bufferSize) buffer.clear();
                    totalLines++;
                }
            }
        } else {
            List<String> lines = Files.readAllLines(inputFile);
            totalLines = lines.size();
        }

        long end = System.currentTimeMillis();
        monitor.stop();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalLinesRead", totalLines);
        metrics.put("fileSizeBytes", Files.size(inputFile));
        metrics.putAll(monitor.getMetrics());

        return new PerformanceResult(getName(), end - start, metrics);
    }

}
