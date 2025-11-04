package com.techsol.web.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.techsol.fileio.FileWriterTest;
import com.techsol.tests.PerformanceResult;
import com.techsol.tests.TestResourceMonitor;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

public class TestRunnerAPI {
    private final TestResourceMonitor resourceMonitor = new TestResourceMonitor();

    @HTTPPath(path = "/api/tests/writeToTestFile")
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
}
