<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
    
    <!-- Output as HTML with indentation -->
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    
    <!-- Strip whitespace from elements -->
    <xsl:strip-space elements="*"/>
    
    <!-- Copy all nodes and attributes by default -->
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Special handling for elements with Alpine.js attributes -->
    <xsl:template match="*[starts-with(@*,'x-')]">
        <xsl:element name="{local-name()}">
            <!-- Copy regular attributes -->
            <xsl:for-each select="@*[not(starts-with(name(),'x-'))]">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            
            <!-- Copy Alpine.js attributes -->
            <xsl:for-each select="@*[starts-with(name(),'x-')]">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            
            <!-- Process child nodes -->
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>