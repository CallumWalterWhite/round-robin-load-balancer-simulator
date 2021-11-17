package loadbalancer_common.XMLParser;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser 
{
    private String _path;

    
    XmlParser(String path)
    {
        _path = path;
    }
    
    public String GetString(String elementPath) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        Document doc = GetXmlDocument();
        doc.getDocumentElement().normalize();
        return XmlLookup(doc, elementPath);
    }
    
    
    public int GetInt(String elementPath) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        Document doc = GetXmlDocument();
        doc.getDocumentElement().normalize();
        String value = XmlLookup(doc, elementPath);
        return Integer.parseInt(value);
    }
    
    public static XmlParser Create(String path)
    {
        return new XmlParser(path);
    }
    
    private Document GetXmlDocument() throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(_path);
        return builder.parse(file);
    }
    
    private String XmlLookup(Document doc, String elementPath) throws Exception
    {
        if (elementPath.length() > 0)
        {
            Node node = ElementLookup(doc.getChildNodes().item(1), elementPath);
            
            return node.getTextContent();
        }
        else
        {
            throw InvalidPathException();
        }
    }
    
    private Node ElementLookup(Node node, String ele) throws Exception
    {
        Node nodeChild = null;
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            Node item = childNodes.item(i);
            if (childNodes.item(i).getNodeName().equals(ele))
            {
                nodeChild = item;
                break;
            }
        }
        if (nodeChild != null)
        {
            return nodeChild;
        }   
        else
        {
            throw InvalidPathException();
        }
    }
    
    private Exception InvalidPathException()
    {
        return new Exception("Element path is empty or is not a valid path");
    }
    
}
