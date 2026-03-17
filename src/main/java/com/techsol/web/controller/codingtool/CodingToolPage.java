package com.techsol.web.controller.codingtool;

import com.techsol.config.Constants;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.templateengine.PebbleEngineProvider;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class CodingToolPage {
    @HTTPPath(path = "/codingtool")
    public void index(HTTPRequest request, HTTPResponse response) throws Exception {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine().getTemplate(Constants.TEMPLATES_PATH + "codingtool" + Constants.FILE_SEPARATOR + "codingtool.peb");
        
        Map<String, Object> context = new HashMap<>();
        
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        
        String output = writer.toString();
        
        response.setCompressContent(true);
        HeaderHelper.createHtmlResponse(output, response);      
    }
}
