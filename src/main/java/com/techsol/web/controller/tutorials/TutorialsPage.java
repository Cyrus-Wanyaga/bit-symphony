package com.techsol.web.controller.tutorials;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.techsol.config.Constants;
import com.techsol.database.dao.ConfigDao;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.template.PebbleTemplate;

public class TutorialsPage {
    @HTTPPath(path = "/tutorials")
    public void index(HTTPRequest request, HTTPResponse httpResponse) throws IOException {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine()
                .getTemplate(Constants.TEMPLATES_PATH + "testcases"
                        + Constants.FILE_SEPARATOR + "testcasedata.peb");

        Map<String, String> configs = ConfigDao.getConfig();

        Map<String, Object> context = new HashMap<>();
        context.put("configs", configs);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        String output = writer.toString();

        httpResponse.setCompressContent(true);
        HeaderHelper.createHtmlResponse(output, httpResponse);
    }
}
