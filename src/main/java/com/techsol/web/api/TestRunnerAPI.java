package com.techsol.web.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.techsol.tests.PerformanceResult;
import com.techsol.tests.TestResourceMonitor;
import com.techsol.tests.file.FileReaderTest;
import com.techsol.tests.file.FileWriterTest;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

public class TestRunnerAPI {
    private final TestResourceMonitor resourceMonitor = new TestResourceMonitor();

    @HTTPPath(path = "/api/tests/writeToFile")
    public void writeToFileTest(HTTPRequest request, HTTPResponse response) throws Exception {
        String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
        JSONObject requestObject = new JSONObject(requestBodyString);
        JSONObject responseObject = new JSONObject();

        Path tmpDir = Paths.get("output");
        if (!Files.exists(tmpDir))
            Files.createDirectories(tmpDir);

        boolean chunked = requestObject.getBoolean("chunked");
        int chunkSize = requestObject.getInt("chunkSize");
        String fileName = "Write_" + (chunked ? "chunked" : "full") + ".txt";
        FileWriterTest fileWriterTest = new FileWriterTest(10000000, chunked, chunkSize, tmpDir.resolve(fileName));

        System.out.println("Running: " + fileWriterTest.getName());
        PerformanceResult result = fileWriterTest.runTest(resourceMonitor);
        JSONObject testResultObject = new JSONObject();
        testResultObject.put("testName", result.getTestName());
        testResultObject.put("durationInMillis", result.getDurationMillis());
        testResultObject.put("fileSize", result.getMetrics().get("fileSizeBytes"));
        testResultObject.put("memoryUsed", result.getMetrics().get("memoryUsedBytes"));
        responseObject.put("result", testResultObject);

        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }

    @HTTPPath(path = "/api/tests/readFromFile")
    public void readFromFileTest(HTTPRequest request, HTTPResponse response) throws Exception {
        String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
        System.out.println("Processing request: " + requestBodyString);
        JSONObject requestObject = new JSONObject(requestBodyString);
        JSONObject responseObject = new JSONObject();

        Path tmpDir = Paths.get("output");
        if (!Files.exists(tmpDir)) {
            Files.createDirectories(tmpDir);
        }

        File[] files = new File(tmpDir.getFileName().toFile().getAbsolutePath()).listFiles();
        if (files.length == 0) {
            responseObject.put("message", "No default test file to read from");
            HeaderHelper.createJsonResponse(responseObject.toString(), response);
            response.setStatusCode(500);
            response.setOk(false);
            return;
        }

        boolean bufferedRead = requestObject.getBoolean("bufferedRead");
        int bufferSize = requestObject.getInt("bufferSize");
        String testFile = files[0].getAbsolutePath();

        FileReaderTest fileReaderTest = new FileReaderTest(tmpDir.resolve(testFile), bufferedRead, bufferSize);
        PerformanceResult result = fileReaderTest.runTest(resourceMonitor);
        JSONObject testResultObject = new JSONObject();
        testResultObject.put("testName", result.getTestName());
        testResultObject.put("durationInMillis", result.getDurationMillis());
        testResultObject.put("fileSize", result.getMetrics().get("fileSizeBytes"));
        testResultObject.put("totalLinesRead", result.getMetrics().get("totalLinesRead"));

        responseObject.put("result", testResultObject);
        
        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }
}
