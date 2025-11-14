package com.techsol.web.api;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.techsol.database.dao.ConfigDao;
import com.techsol.database.dao.TestSessionDao;
import com.techsol.models.TestSession;
import com.techsol.tests.PerformanceResult;
import com.techsol.tests.TestResourceMonitor;
import com.techsol.tests.computation.ComputationPerformanceTest;
import com.techsol.tests.computation.ComputationPerformanceTest.Mode;
import com.techsol.tests.file.FileReaderTest;
import com.techsol.tests.file.FileWriterTest;
import com.techsol.utils.directory.DirectoryManager;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;

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
        try {
            String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
            System.out.println("Request body: " + requestBodyString);
            JSONObject requestObject = new JSONObject(requestBodyString);
            JSONObject responseObject = new JSONObject();

            String defaultDirectory = ConfigDao.getDefaultDirectory();
            System.out.println("The default directory is: " + defaultDirectory);

            Path tmpDir = Paths.get(defaultDirectory.isBlank() ? "output" : defaultDirectory);
            if (!Files.exists(tmpDir)) {
                Files.createDirectories(tmpDir);
            }

            String sizethresholdMB = ConfigDao.getDirectoryMaxSize();
            if (sizethresholdMB.isEmpty() || sizethresholdMB.isBlank()) {
                responseObject.put("message", "Output directory missing size threshold");
                HeaderHelper.createJsonResponse(responseObject.toString(), response);
                response.setStatusCode(HTTPStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
                response.setOk(false);
                return;
            }

            long sizeThresholdBytes = Long.valueOf(sizethresholdMB.replace("MB", "")) * 1024 * 1024;

            DirectoryManager.DirectoryCleanupResult dirInfo = DirectoryManager.manageOutputDirectory(tmpDir,
                    sizeThresholdBytes);
            System.out.println("DirInfo: " + dirInfo.getNextFileIndex());
            int startIndex = dirInfo.getNextFileIndex();

            boolean chunked = requestObject.getBoolean("chunked");
            int chunkSize = requestObject.getInt("chunkSize");
            int testRepetitions = requestObject.getInt("testRepetitions");
            int numberOfItems = requestObject.getInt("numberOfItems");
            String dataType = requestObject.getString("dataType");
            String fileName = "Write_" + (chunked ? "chunked" : "full") + ".txt";

            TestSession testSession = new TestSession();
            testSession.setTestGroup("Write To File");
            testSession.setDescription(
                    "Write to a file with " + numberOfItems + (dataType.equals("text") ? " words" : " numbers"));
            testSession.setTotalRuns(testRepetitions);

            int testSessionId = TestSessionDao.createTestSession(testSession);
            if (testSessionId <= 0) {
                responseObject.put("message", "Failed to create test session");
                HeaderHelper.createJsonResponse(responseObject.toString(), response);
                response.setStatusCode(HTTPStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
                response.setOk(false);
                return;
            }

            JSONArray testResultsArray = new JSONArray();
            if (testRepetitions == 0) {
                testRepetitions = 1;
            }
            for (int i = 0; i < testRepetitions; i++) {
                String newFileName = chunked
                        ? fileName.replace("chunked.txt", "chunked_" + startIndex + ".txt")
                        : fileName.replace("full.txt", "full_" + startIndex + ".txt");
                FileWriterTest fileWriterTest = new FileWriterTest(numberOfItems, chunked, chunkSize,
                        tmpDir.resolve(newFileName),
                        testSessionId, dataType);

                System.out.println("Running: " + fileWriterTest.getName());

                PerformanceResult result = fileWriterTest.runTest(resourceMonitor);
                if (result == null) {
                    return;
                }

                JSONObject testResultObject = new JSONObject();
                testResultObject.put("testName", result.getTestName());
                testResultObject.put("durationInMillis", result.getDurationMillis());
                testResultObject.put("fileSize", result.getMetrics().get("fileSizeBytes"));
                testResultObject.put("memoryUsed", result.getMetrics().get("memoryUsedBytes"));
                testResultObject.put("testIndex", i);
                testResultsArray.put(testResultObject);

                startIndex += 1;
            }
            responseObject.put("result", testResultsArray);
            HeaderHelper.createJsonResponse(responseObject.toString(), response);
        } catch (JSONException e) {
            e.printStackTrace();

            HeaderHelper.createJsonResponse("{\"message\":\"Please check the payload\"}", response);
            response.setStatusCode(HTTPStatusCode.BAD_REQUEST.getStatusCode());
            response.setOk(false);
            return;
        }
    }

    @HTTPPath(path = "/api/tests/readFromFile")
    public void readFromFileTest(HTTPRequest request, HTTPResponse response) throws Exception {
        String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
        JSONObject requestObject = new JSONObject(requestBodyString);
        JSONObject responseObject = new JSONObject();

        String defaultDirectory = ConfigDao.getDefaultDirectory();
        Path tmpDir = Paths.get(defaultDirectory.isBlank() ? "output" : defaultDirectory);
        if (!Files.exists(tmpDir)) {
            responseObject.put("message", "No default directory for test files");
            HeaderHelper.createJsonResponse(responseObject.toString(), response);
            response.setStatusCode(HTTPStatusCode.NOT_FOUND.getStatusCode());
            response.setOk(false);
            return;
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
