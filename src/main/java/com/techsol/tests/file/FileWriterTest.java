package com.techsol.tests.file;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.techsol.app.WordListLoader;
import com.techsol.database.dao.TestFileDao;
import com.techsol.database.dao.TestResultDao;
import com.techsol.models.TestFile;
import com.techsol.models.TestResult;
import com.techsol.tests.PerformanceResult;
import com.techsol.tests.PerformanceTest;
import com.techsol.tests.TestResourceMonitor;

public class FileWriterTest implements PerformanceTest {
    private final int numberOfItems;
    private final boolean chunked;
    private final int chunkSize;
    private final Path outputFile;
    private final int testSessionId;
    private final String dataType;

    public FileWriterTest(int numberOfItems, boolean chunked, int chunkSize, Path outputFile, int testSessionId,
            String dataType) {
        this.numberOfItems = numberOfItems;
        this.chunked = chunked;
        this.chunkSize = chunkSize;
        this.outputFile = outputFile;
        this.testSessionId = testSessionId;
        this.dataType = dataType;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return Map.of(
                "numberOfItems", numberOfItems,
                "chunked", chunked,
                "chunkSize", chunkSize,
                "outputFile", outputFile.toString(),
                "dataType", dataType);
    }

    @Override
    public String getName() {
        return "File Write Test (" + (chunked ? "chunked" : "single") + ")";
    }

    @Override
    public PerformanceResult runTest(TestResourceMonitor monitor) throws Exception {
        monitor.start();

        long start = System.currentTimeMillis();
        System.out.println("Start at: " + start);
        Random rand = new Random();

        List<String> words = null;

        if (dataType.equals("text")) {
            words = new ArrayList<>();
            words = new WordListLoader().getWordList();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            if (chunked) {
                StringBuilder chunk = new StringBuilder();
                for (int i = 1; i <= numberOfItems; i++) {
                    if (dataType.equals("text")) {
                        chunk.append(words.get(rand.nextInt(words.size()))).append("\n");
                    } else {
                        chunk.append(rand.nextInt(1_000_000)).append("\n");
                    }
                    if (i % chunkSize == 0 || i == numberOfItems) {
                        writer.write(chunk.toString());
                        chunk.setLength(0);
                    }
                }
            } else {
                for (int i = 0; i < numberOfItems; i++) {
                    if (dataType.equals("text")) {
                        writer.write(words.get(rand.nextInt(words.size())));
                    } else {
                        writer.write(rand.nextInt(1_000_000) + "\n");
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("End at: " + end);
        monitor.stop();

        TestFile testFile = new TestFile();
        testFile.setFileName(outputFile.getFileName().toString());
        testFile.setFilePath(outputFile.toAbsolutePath().toString());
        testFile.setFileExists(1);
        testFile.setFileSizeInBytes(Files.size(outputFile));
        testFile.setNumberOfItems(numberOfItems);
        testFile.setDataType(dataType);
        testFile.setCreatedAt(new Date());
        testFile.setUpdatedAt(new Date());

        int testFileId = TestFileDao.saveTestFile(testFile);
        if (testFileId <= 0) {
            System.out.println("Failed to save test file generation data");
            return null;
        }

        TestResult testResult = new TestResult();
        testResult.setTestName(getName());
        testResult.setAlgorithmName("File I/O Ops");
        testResult.setStartTime(String.valueOf(start));
        testResult.setEndTime(String.valueOf(end));
        testResult.setChunkedRead(-1);
        testResult.setChunkedWrite(chunked ? 1 : 0);
        testResult.setDurationMs(end - start);
        testResult.setCpuUsage((double) monitor.getMetrics().get("cpuLoad"));
        testResult.setMemoryUsage((long) monitor.getMetrics().get("memoryUsedBytes"));
        testResult.setDiskIO(0);
        testResult.setExtraInfo("");
        testResult.setSessionId(testSessionId);
        testResult.setFileId(testFileId);

        boolean savedTestResult = TestResultDao.createTestResult(testResult);
        if (!savedTestResult)
            return null;

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("fileSizeBytes", Files.size(outputFile));
        metrics.putAll(monitor.getMetrics());

        return new PerformanceResult(getName(), end - start, metrics);
    }

}
