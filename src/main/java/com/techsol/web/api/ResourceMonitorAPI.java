package com.techsol.web.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.techsol.utils.ResourceMonitor;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;

public class ResourceMonitorAPI {
    @HTTPPath(path = "/api/resource_monitor/os_details")
    public void osDetails(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        JSONObject responseObject = new JSONObject();
        ResourceMonitor resourceMonitor = new ResourceMonitor();
        responseObject.put("os", resourceMonitor.getOS());
        responseObject.put("jvm", resourceMonitor.getJVMVersion());
        responseObject.put("osArch", resourceMonitor.getOsArch());
        responseObject.put("availableProcessors", resourceMonitor.getAvailableProcessors());
        String response = responseObject.toString();

        Map<String, String> headers = new HashMap<>();
        httpResponse.setStatusCode(200);
        httpResponse.setOk(true);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + " " + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(response.length()));
        httpResponse.setHeaders(headers);
        System.out.println(response);
        httpResponse.setBody(response.getBytes());
    }

    @HTTPPath(path = "/api/resource_monitor/memory_details")
    public void memoryDetails(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        JSONObject responseObject = new JSONObject();
        ResourceMonitor resourceMonitor = new ResourceMonitor();
        responseObject.put("freeMemory", Double.parseDouble(resourceMonitor.getFreeMemory()) / 1024 / 1024 / 1024);
        responseObject.put("maxMemory", Double.parseDouble(resourceMonitor.getMaxMemory()) / 1024 / 1024 / 1024);
        responseObject.put("usedMemory", Double.parseDouble(resourceMonitor.getUsedMemory()) / 1024 / 1024 / 1024);

        Map<String, String> headers = new HashMap<>();
        httpResponse.setStatusCode(200);
        httpResponse.setOk(true);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + " " + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(responseObject.toString().length()));
        httpResponse.setHeaders(headers);
        httpResponse.setBody(responseObject.toString().getBytes());
    }
}
