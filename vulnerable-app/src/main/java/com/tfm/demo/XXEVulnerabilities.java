package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.File;

/**
 * VULNERABILIDAD: XXE (XML External Entity)
 * 
 * OWASP Top 10 2021: A05:2021 - Security Misconfiguration
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Reglas SonarQube:
 * - S2755: XML parsers should not be vulnerable to XXE attacks (BLOCKER)
 * - S4435: XML transformers should be secured (BLOCKER)
 * - S5594: XML external entity processing should be disabled (CRITICAL)
 */
public class XXEVulnerabilities {
    
    /**
     * Vulnerabilidad S2755: DocumentBuilder sin protección XXE
     * Permite leer archivos locales y SSRF
     */
    public String parseXMLFromRequest(HttpServletRequest request) throws Exception {
        String xmlContent = request.getParameter("xml");
        
        // VULNERABLE: Parser sin deshabilitar external entities
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        org.w3c.dom.Document doc = builder.parse(
            new InputSource(new StringReader(xmlContent))
        );
        
        return doc.getDocumentElement().getTextContent();
        
        /* Ataque:
        <?xml version="1.0"?>
        <!DOCTYPE foo [
          <!ENTITY xxe SYSTEM "file:///etc/passwd">
        ]>
        <data>&xxe;</data>
        */
    }
    
    /**
     * Vulnerabilidad: SAXParser sin protección
     */
    public void parseSAX(String xmlData) throws Exception {
        // VULNERABLE: SAX parser sin configuración segura
        javax.xml.parsers.SAXParserFactory spf = 
            javax.xml.parsers.SAXParserFactory.newInstance();
        javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
        
        org.xml.sax.helpers.DefaultHandler handler = new org.xml.sax.helpers.DefaultHandler();
        saxParser.parse(new InputSource(new StringReader(xmlData)), handler);
        
        /* Ataque - Billion Laughs (DoS):
        <!DOCTYPE lolz [
          <!ENTITY lol "lol">
          <!ENTITY lol2 "&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;">
          <!ENTITY lol3 "&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;">
        ]>
        <lolz>&lol3;</lolz>
        */
    }
    
    /**
     * Vulnerabilidad: XMLReader sin securizar
     */
    public void parseWithXMLReader(String xml) throws Exception {
        // VULNERABLE: XMLReader acepta external entities
        org.xml.sax.XMLReader reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
        reader.parse(new InputSource(new StringReader(xml)));
        
        /* Ataque - SSRF:
        <!DOCTYPE foo [
          <!ENTITY xxe SYSTEM "http://internal-server/admin">
        ]>
        <data>&xxe;</data>
        */
    }
    
    /**
     * Vulnerabilidad: Transformer sin protección
     */
    public String transformXML(String xmlInput, String xslInput) throws Exception {
        // VULNERABLE: Transformer puede ejecutar código
        javax.xml.transform.TransformerFactory factory = 
            javax.xml.transform.TransformerFactory.newInstance();
        
        javax.xml.transform.Transformer transformer = factory.newTransformer(
            new javax.xml.transform.stream.StreamSource(new StringReader(xslInput))
        );
        
        javax.xml.transform.stream.StreamSource source = 
            new javax.xml.transform.stream.StreamSource(new StringReader(xmlInput));
        javax.xml.transform.stream.StreamResult result = 
            new javax.xml.transform.stream.StreamResult(new java.io.StringWriter());
        
        transformer.transform(source, result);
        return result.getWriter().toString();
        
        /* Ataque - XSLT puede ejecutar código Java:
        <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:java="http://xml.apache.org/xslt/java">
          <xsl:template match="/">
            <xsl:value-of select="java:java.lang.Runtime.getRuntime().exec('whoami')"/>
          </xsl:template>
        </xsl:stylesheet>
        */
    }
    
    /**
     * Vulnerabilidad: Procesamiento de SOAP sin validación
     */
    public void processSOAPMessage(String soapMessage) throws Exception {
        // VULNERABLE: SOAP message processor
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(
            new InputSource(new StringReader(soapMessage))
        );
        
        // Procesa SOAP envelope sin validar external entities
    }
    
    /**
     * Vulnerabilidad: Upload y parse de XML
     */
    public String processUploadedXML(HttpServletRequest request) throws Exception {
        String xmlFile = request.getParameter("xmlFile");
        
        // VULNERABLE: Parse archivo subido sin validar
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        org.w3c.dom.Document doc = builder.parse(new File(xmlFile));
        return doc.getDocumentElement().getNodeName();
        
        /* Usuario puede subir:
        <!DOCTYPE foo [
          <!ENTITY xxe SYSTEM "file:///app/config/database.properties">
        ]>
        <config>&xxe;</config>
        */
    }
    
    /**
     * Vulnerabilidad: XML Schema validation insegura
     */
    public boolean validateXMLSchema(String xml, String xsdPath) throws Exception {
        // VULNERABLE: Schema validation sin protección XXE
        javax.xml.validation.SchemaFactory sf = 
            javax.xml.validation.SchemaFactory.newInstance(
                javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        
        javax.xml.validation.Schema schema = sf.newSchema(new File(xsdPath));
        javax.xml.validation.Validator validator = schema.newValidator();
        
        validator.validate(new javax.xml.transform.stream.StreamSource(
            new StringReader(xml)));
        
        return true;
    }
    
    /**
     * Vulnerabilidad: XPath injection + XXE
     */
    public String xpathQuery(String xmlData, String userQuery) throws Exception {
        // VULNERABLE: Doble problema - XXE + XPath injection
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(
            new InputSource(new StringReader(xmlData))
        );
        
        // VULNERABLE: XPath con input del usuario
        javax.xml.xpath.XPath xpath = 
            javax.xml.xpath.XPathFactory.newInstance().newXPath();
        String query = "//user[@name='" + userQuery + "']";
        
        return xpath.evaluate(query, doc);
        // Ataque XPath: ' or '1'='1
    }
}
