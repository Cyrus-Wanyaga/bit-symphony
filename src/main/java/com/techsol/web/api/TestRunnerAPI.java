package com.techsol.web.api;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import com.techsol.tests.PerformanceResult;
import com.techsol.tests.TestResourceMonitor;
import com.techsol.tests.computation.ComputationPerformanceTest;
import com.techsol.tests.computation.ComputationPerformanceTest.Mode;
import com.techsol.tests.file.FileReaderTest;
import com.techsol.tests.file.FileWriterTest;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

public class TestRunnerAPI {
    private final TestResourceMonitor resourceMonitor = new TestResourceMonitor();

    @HTTPPath(path = "/api/tests/getComputationalTestModes")
    public void getComputationalTestModes(HTTPRequest request, HTTPResponse response) {
        Mode[] modes = ComputationPerformanceTest.Mode.values();
        JSONArray modeJsonArray = new JSONArray();
        for (Mode mode : modes) {
            modeJsonArray.put(mode);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", modeJsonArray);

        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }

    @HTTPPath(path = "/api/tests/getTestFiles")
    public void getTestFiles(HTTPRequest request, HTTPResponse response) {
        JSONObject responseObject = new JSONObject();
        JSONArray fileJsonArray = new JSONArray();
        File[] files = new File("output").listFiles();
        for (File file : files) {
            fileJsonArray.put(file.getAbsolutePath());
        }
        responseObject.put("result", fileJsonArray);
        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }

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
        FileWriterTest fileWriterTest = new FileWriterTest(1_000, chunked, chunkSize, tmpDir.resolve(fileName));

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

    @HTTPPath(path = "/api/tests/computational")
    public void performComputationalTests(HTTPRequest request, HTTPResponse response) throws Exception {
        String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
        System.out.println("Processing request: " + requestBodyString);
        JSONObject requestObject = new JSONObject(requestBodyString);
        JSONObject responseObject = new JSONObject();

        Path tmpDir = Paths.get("output");
        if (!Files.exists(tmpDir)) {
            Files.createDirectories(tmpDir);
        }

        String testMode = requestObject.getString("testMode");

        File[] files = new File(tmpDir.getFileName().toFile().getAbsolutePath()).listFiles();
        if (files.length == 0) {
            responseObject.put("message", "No default test file to read from");
            HeaderHelper.createJsonResponse(responseObject.toString(), response);
            response.setStatusCode(500);
            response.setOk(false);
            return;
        }

        String testFile = files[0].getAbsolutePath();
        ComputationPerformanceTest computationPerformanceTest = new ComputationPerformanceTest(tmpDir.resolve(testFile),
                Mode.valueOf(testMode), 0);
        PerformanceResult result = computationPerformanceTest.runTest(resourceMonitor);
        JSONObject resultJson = new JSONObject();
        resultJson.put("testName", result.getTestName());
        resultJson.put("durationMillis", result.getDurationMillis());
        resultJson.put("metrics", result.getMetrics());

        responseObject.put("result", resultJson);

        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }

}
