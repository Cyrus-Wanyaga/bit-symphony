package com.techsol.app;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class TemplateRenderer {

    private final PebbleEngine  pebbleEngine;
    private final Path templateDirectory;

    public TemplateRenderer(String templateDirectoryPath) {
        this.pebbleEngine = new PebbleEngine.Builder().build();
        this.templateDirectory = Paths.get(templateDirectoryPath);
        if(!Files.exists(this.templateDirectory) || !Files.isDirectory(this.templateDirectory)) {
            throw new IllegalArgumentException("Template directory " + templateDirectoryPath + " does not exist or is not a directory.");
        }
    }

    public String render(String templatePath, Map<String, Object> context) throws IOException {
        Path fullTemplatePath = templateDirectory.resolve(templatePath);
        if (!Files.exists(fullTemplatePath)) {
            throw new IllegalArgumentException("Template file " + templatePath + " not found in " + templateDirectory);
        }

        String templateContent = Files.readString(fullTemplatePath);
        System.out.println("template content : " + templateContent);

        PebbleTemplate compiledTemplate = pebbleEngine.getTemplate(fullTemplatePath.toFile().getAbsolutePath());

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        return writer.toString();
    }

    public String renderFromContent(String templateContent, Map<String, Object> context) throws IOException {
        PebbleTemplate compiledTemplate = pebbleEngine.getTemplate(templateContent);

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        return writer.toString();
    }

    public static void main(String[] args) throws IOException {
        // Example usage:
        String templateDir = "src/main/resources/templates"; // Path to your templates directory
        TemplateRenderer renderer = new TemplateRenderer(templateDir);

        Map<String, Object> data = Map.of("name", "John Doe", "age", 30, "items", new String[]{"item1", "item2", "item3"});

        // Create a test template
        String testTemplatePath = "test.html";
        Path testTemplateFile = Paths.get(templateDir, testTemplatePath);
        String testTemplateContent = "<h1>Hello, {{ name }}!</h1><p>You are {{ age }} years old.</p><ul>{% for item in items %}<li>{{ item }}</li>{% endfor %}</ul>";
        Files.writeString(testTemplateFile, testTemplateContent);

        String renderedTemplate = renderer.render("/Users/georgegithogori/Cyrus Work/Java Projects/bit-symphony/" + templateDir + File.separator + testTemplatePath, data);
        System.out.println(renderedTemplate);

                // Example of rendering from content
        // String templateContent = "<h1>Hello from content {{ name }}</h1>";
        // String renderedFromContent = renderer.renderFromContent(templateContent, data);
        // System.out.println(renderedFromContent);

        //Delete test file
        Files.delete(testTemplateFile);
    }
}
