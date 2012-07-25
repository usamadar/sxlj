/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sxlj.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author usamadar
 */
public class XMLHelper {
    /**
     * This function replaces a text or xml node in the xml document with the provided
     * fragment. The node to be replaced must be specified as the XPATH.
     * @param document document to search in
     * @param xPathStr XPath to find the node
     * @param fragment fragment(XML or text) to replace with
     * @return a new XML document as string
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws TransformerException 
     */
    public static String replaceNode(String document, String xPathStr, String fragment) throws SAXException, IOException, ParserConfigurationException,
            XPathExpressionException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream((document.getBytes())));
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        XPathExpression expr = xPath.compile(xPathStr);

        Object o = expr.evaluate(doc, XPathConstants.NODE);

        if (o == null) {
            return null;
        }
        if (o instanceof Element) {
            DocumentBuilder builder2 = factory.newDocumentBuilder();
            Document fragmentDoc = builder2.parse(new ByteArrayInputStream(fragment.getBytes()));
            String fdoc = docToString(fragmentDoc);
            Node injectedNode = doc.adoptNode(fragmentDoc.getFirstChild());
            Node nodeFound = (Node) o;
            Node parentNode = nodeFound.getParentNode();
            parentNode.replaceChild(injectedNode, nodeFound);

        } else if (o instanceof Text) {
            ((Text) o).setData(fragment);
        }
        return (docToString(doc));
    }
    /**
     * Given an XML  Document object, convert it to string
     * @param doc
     * @return document as string
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public static String docToString(Document doc) throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc); 
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(domSource, result);
        return (result.getWriter().toString());
    }
    
    /**
     * Given a XML document object as String, and an XPath, the function deletes
     * the node found through the XPath evaluation and returns the modified document
     * as a string
     * @param document
     * @param xPathStr
     * @return Modified document as string
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws TransformerException 
     */
    public static String deleteNode(String document, String xPathStr) throws SAXException, IOException, ParserConfigurationException,
            XPathExpressionException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream((document.getBytes())));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xPath.compile(xPathStr);

        Object o = expr.evaluate(doc, XPathConstants.NODE);
        if (o == null) {
            return null;
        }
        if (o instanceof Element) {
            Element nodeFound = (Element) o;
            Node parentNode = nodeFound.getParentNode();
            parentNode.removeChild(nodeFound);
        } else if (o instanceof Text) {
            ((Text) o).setData("");
        }
        return (docToString(doc));
    }
    /**
     * Given an XML document as string and an XPATH, this function returns
     * the node found by evaluating the XPath
     * @param document
     * @param xPathStr
     * @return The Node (XML or Text) found through XPath as String 
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws TransformerException 
     */
    public static String getNodeAsStr(String document, String xPathStr) throws SAXException, IOException, ParserConfigurationException,
            XPathExpressionException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream((document.getBytes())));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xPath.compile(xPathStr);
        String data = null;
        Object o = expr.evaluate(doc, XPathConstants.NODE);
        if (o instanceof Element) {
            Node node = (Node) o;
            data = nodeToString(node);
        } else if (o instanceof Text) {
            data = ((Text) o).getData();
        }
        return (data);
    }
    /**
     * Given a Node object, returns its string representation
     * @param node
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public static String nodeToString(Node node) throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(node);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(domSource, result);
        return (result.getWriter().toString());
    }    
}
