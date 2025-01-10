package com.techsol.web.controller.main;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.techsol.config.Constants;
import com.techsol.database.dao.ConfigDao;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.utils.transform.IndentHTML;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.template.PebbleTemplate;

public class MainPage {
    /**
     * Handles the GET / route, renders the main page.
     * 
     * @param httpRequest The request object
     * @param httpResponse The response object
     * @throws IOException If there is an IO error
     * @throws TransformerException If there is an error transforming the HTML
     */
    @HTTPPath(path = "/")
    public void index(HTTPRequest httpRequest, HTTPResponse httpResponse) throws IOException, TransformerException {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine()
                .getTemplate(Constants.TEMPLATES_PATH + "main"
                        + Constants.FILE_SEPARATOR + "main.peb");
                        
        Map<String, String> configs = ConfigDao.getConfig();

        Map<String, Object> context = new HashMap<>();
        context.put("configs", configs);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        String output = writer.toString();

        IndentHTML.setInput(output);
        String indentedOutput = IndentHTML.transformInput();
        httpResponse.setCompressContent(true);
        HeaderHelper.createHtmlResponse(indentedOutput, httpResponse);
    }
}
