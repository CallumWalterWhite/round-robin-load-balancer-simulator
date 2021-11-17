/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadbalancer_common.ConfigurationManager;

/**
 *
 * @author callu
 */
public interface IConfigurationManager 
{
    public int GetDefaultPort();
    
    public int GetDatagramPort();
    
    public int GetConfigIntValue(String key);
    
    public String GetConfigValue(String key);
}
