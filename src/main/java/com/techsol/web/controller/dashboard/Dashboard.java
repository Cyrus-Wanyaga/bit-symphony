package com.techsol.web.controller.dashboard;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.techsol.config.Constants;
import com.techsol.utils.headers.HeaderHelper;
import com.techsol.web.annotations.HTTPPath;
import com.techsol.web.http.HTTPRequest;
import com.techsol.web.http.HTTPResponse;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.template.PebbleTemplate;

public class Dashboard {
    private final List<UserDTO> users = Arrays.asList(
        new UserDTO(1, "John Doe", "john@example.com"),
        new UserDTO(2, "Jane Smith", "jane@example.com")
    );

    @HTTPPath(path = "/dashboard")
    public void index(HTTPRequest request, HTTPResponse httpResponse) throws IOException, TransformerException {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine().getTemplate(Constants.TEMPLATES_PATH + "dashboard" + Constants.FILE_SEPARATOR + "dashboard.peb");

        Map<String, Object> context = new HashMap<>();
        context.put("websiteTitle", "Wow...this is nice");
        context.put("users", users);
        context.put("requestPath", request.getUrl());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        String output = writer.toString();

        httpResponse.setCompressContent(true);
        HeaderHelper.createHtmlResponse(output, httpResponse);
    }

    private class UserDTO {
        private int id;
        private String name;
        private String email;

        public UserDTO(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }
}
