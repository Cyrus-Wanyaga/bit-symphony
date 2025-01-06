package com.techsol.utils.validator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.techsol.config.Constants;
import com.techsol.web.templateengine.PebbleEngineProvider;

import io.pebbletemplates.pebble.template.PebbleTemplate;

public class XmlValidator {
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]*)\"|'([^']*)')");

    public static void validateXmlInHtml(String htmlContent) throws IOException {
        Document doc = Jsoup.parse(htmlContent, Parser.htmlParser());

        // Select all elements (including those within <script> and <style>)
        // Select all elements EXCEPT <!DOCTYPE>
        Elements elements = doc.select("*"); // Select all elements
        elements.removeIf(e -> e.nodeName().equalsIgnoreCase("#doctype")); // Remove doctype

        for (Element element : elements) {
            validateElement(element);
        }
    }

    private static void validateElement(Element element) {
        String outerHtml = element.outerHtml();

        if (element.tagName().contains("root")) {
            return;
        }

        // 1. Self-closing check (Simplified - Jsoup handles this mostly)
        if (isVoidElement(element.tagName())) {
            return; // It's self-closing, nothing more to check
        }

        // 2. Closing tag check (Using a stack for better depth tracking)
        Deque<String> tagStack = new ArrayDeque<>();
        tagStack.push(element.tagName());

        String closingTag = "</" + element.tagName() + ">";
        if (!outerHtml.contains(closingTag)) {
            System.err.println("Error: Missing closing tag for <" + element.tagName() + "> in: " + outerHtml);
        }

        // 3. Attribute validation and escaping
        String attributes = element.attributes().html();
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributes);
        while (matcher.find()) {
            String attributeValue = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
            String escapedValue = escapeXml(attributeValue);
            if (!escapedValue.equals(attributeValue)) {
                System.out.println("Attribute value changed: " + attributeValue + " -> " + escapedValue);
            }
        }
    }

    private static boolean isVoidElement(String tagName) {
        return tagName.equalsIgnoreCase("area") ||
                tagName.equalsIgnoreCase("base") ||
                tagName.equalsIgnoreCase("path") ||
                tagName.equalsIgnoreCase("br") ||
                tagName.equalsIgnoreCase("col") ||
                tagName.equalsIgnoreCase("embed") ||
                tagName.equalsIgnoreCase("hr") ||
                tagName.equalsIgnoreCase("img") ||
                tagName.equalsIgnoreCase("input") ||
                // tagName.equalsIgnoreCase("link") ||
                tagName.equalsIgnoreCase("meta") ||
                tagName.equalsIgnoreCase("param") ||
                tagName.equalsIgnoreCase("source") ||
                tagName.equalsIgnoreCase("track") ||
                tagName.equalsIgnoreCase("wbr");
    }

    private static String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static void main(String[] args) throws IOException {
        PebbleTemplate template = new PebbleEngineProvider().getPebbleEngine()
                .getTemplate(Constants.TEMPLATES_PATH + "main"
                        + Constants.FILE_SEPARATOR + "main.peb");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        String output = writer.toString();
        validateXmlInHtml(output);
    }

}
