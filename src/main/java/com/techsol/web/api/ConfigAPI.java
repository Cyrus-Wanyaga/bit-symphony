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
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                String value = requestObject.getString(key);
                String newKey;

                if (key.toLowerCase().contains("defaultdirectory")) {
                    newKey = "default_directory";
                } else if (key.toLowerCase().contains("chunksize")) {
                    newKey = "chunk_size";
                } else if (key.toLowerCase().contains("intervaltime")) {
                    newKey = "test_interval";
                } else {
                    newKey = key;
                }

                ConfigDao.updateConfig(newKey, value);
            }
        } catch (Exception e) {
            e.printStackTrace();

            responseObject.put("statusMessage", "Failed to save configs");
            HeaderHelper.createJsonResponse(requestBodyString, response);
            response.setStatusCode(500);
            response.setOk(false);
            return;
        }

        responseObject.put("statusMessage", "Saved configs successfully");
        HeaderHelper.createJsonResponse(responseObject.toString(), response);
    }
}
