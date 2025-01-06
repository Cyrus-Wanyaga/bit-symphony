package com.techsol.utils.transform;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

public class TransformLoader {
    private final TransformerFactory transformerFactory;
    private Transformer transformer;
    private String xslt;

    public TransformLoader(String xslt) throws TransformerConfigurationException {
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xslt)));
    }

    public TransformerFactory getTransformerFactory() {
        return transformerFactory;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public String getXslt() {
        return xslt;
    }

    public void setXslt(String xslt) {
        this.xslt = xslt;
    }

    
}
