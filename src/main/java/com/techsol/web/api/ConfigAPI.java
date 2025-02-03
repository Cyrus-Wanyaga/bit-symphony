package com.techsol.web.api;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.json.JSONObject;

import com.techsol.database.dao.ConfigDao;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;

public class ConfigAPI {
    @HTTPPath(path = "/api/config/save")
    public void saveConfigs(HTTPRequest request, HTTPResponse response) {
        String requestBodyString = new String(request.getBody(), StandardCharsets.UTF_8);
        JSONObject requestObject = new JSONObject(requestBodyString);
        JSONObject responseObject = new JSONObject();

        Iterator<String> keys = requestObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = requestObject.getString(key);
            ConfigDao.updateConfig(key, value);
        }

        responseObject.put("statusMessage", "Saved configs successfully");
        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }
}
