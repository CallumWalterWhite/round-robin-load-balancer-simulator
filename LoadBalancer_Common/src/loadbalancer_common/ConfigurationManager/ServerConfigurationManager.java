package loadbalancer_common.ConfigurationManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import loadbalancer_common.XMLParser.XmlParser;
import org.xml.sax.SAXException;

class ServerConfigurationManager implements IConfigurationManager
{
    private final static String DEFAULTPATH = "appconfig.xml";
    
    private String _path;

    ServerConfigurationManager(String configPath) 
    {
        _path = configPath.length() > 0 ? configPath : DEFAULTPATH;
    }

    @Override
    public int GetDefaultPort() 
    {
        return GetConfigIntValue("port");
    }

    @Override
    public int GetConfigIntValue(String key) 
    {
        try 
        {
            return XmlParser.Create(_path).GetInt(key);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.FINE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public String GetConfigValue(String key) 
    {
        try 
        {
            return XmlParser.Create(_path).GetString(key);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.FINE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int GetDatagramPort() {
        return GetConfigIntValue("datagramPort");
    }
    
}
