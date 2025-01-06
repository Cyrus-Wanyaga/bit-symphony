package com.techsol.web.controller.main;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.json.JSONObject;

import com.techsol.config.Constants;
import com.techsol.database.dao.ConfigDao;
import com.techsol.utils.transform.IndentHTML;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.http.HTTPStatusCode;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.template.PebbleTemplate;

public class MainPage {
    @HTTPPath(path = "/")
    public void index(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException, TransformerException {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine()
                .getTemplate(Constants.TEMPLATES_PATH + "main"
                        + Constants.FILE_SEPARATOR + "main.peb");
                        

        Map<String, String> configs = ConfigDao.getConfig();

        if (configs == null) {
            System.out.println("No configs");
        } else {
            System.out.println("Configs are : " + configs.toString());
        }

        JSONObject configsObject = new JSONObject();

        for (Map.Entry<String, String> entry : configs.entrySet()) {
            configsObject.put(entry.getKey(), entry.getValue());
        }

        Map<String, Object> context = new HashMap<>();
        context.put("configs", configsObject);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        String output = writer.toString();

        IndentHTML.setInput(output);
        String indentedOutput = IndentHTML.transformInput();
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/html");
        responseHeaders.put("Content-Length", String.valueOf(indentedOutput.length()));
        httpResponse.setHeaders(responseHeaders);
        httpResponse.setBody(indentedOutput.getBytes());
        httpResponse.setStatusCode(200);
        httpResponse.setOk(true);
        httpResponse.setStatusLine("HTTP/1.1 " + httpResponse.getStatusCode() + " "
                + HTTPStatusCode.fromStatusCode(httpResponse.getStatusCode()).getReasonPhrase());
    }
}
