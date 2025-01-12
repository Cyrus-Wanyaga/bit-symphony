package com.techsol.web.api;

import org.json.JSONObject;

import com.techsol.utils.ResourceMonitor;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

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

        HeaderHelper.createJsonResponse(response, httpResponse);
    }

    @HTTPPath(path = "/api/resource_monitor/memory_details")
    public void memoryDetails(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        JSONObject responseObject = new JSONObject();
        ResourceMonitor resourceMonitor = new ResourceMonitor();
        responseObject.put("freeMemory", Double.parseDouble(resourceMonitor.getFreeMemory()) / 1024 / 1024 / 1024);
        responseObject.put("maxMemory", Double.parseDouble(resourceMonitor.getMaxMemory()) / 1024 / 1024 / 1024);
        responseObject.put("usedMemory", Double.parseDouble(resourceMonitor.getUsedMemory()) / 1024 / 1024 / 1024);

        HeaderHelper.createJsonResponse(responseObject.toString(), httpResponse);
    }
}
