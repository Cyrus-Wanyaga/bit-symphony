package com.techsol.utils.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class IndentHTML {
    private static Transformer transformer;
    private static String input;

    public IndentHTML(){}

    public static String getInput() {
        return input;
    }

    public static void setInput(String input) {
        IndentHTML.input = input;
    }

    public static String transformInput() throws IOException, TransformerException {
        if (transformer == null) {
            InputStream inputStream = IndentHTML.class.getClassLoader().getResourceAsStream("transform/indenthtml.xslt");
            if (inputStream == null) {
                return null;
            }

            String transformXSLT = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            TransformLoader transformLoader = new TransformLoader(transformXSLT);
            transformer = transformLoader.getTransformer();
        }

        // System.out.println("HTML output: \n" + input);
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(input)), new StreamResult(writer));
        return writer.toString();
    }
}
