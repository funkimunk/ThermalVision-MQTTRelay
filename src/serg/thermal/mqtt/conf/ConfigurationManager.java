package serg.thermal.mqtt.conf;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.lang.reflect.*;

/**
 *
 * @author Joe
 */
public class ConfigurationManager {
    
    
	//Config values
	//The relay thread interval
	public long threadInterval = 300;
	
	//MQTT Broker URI, USN + PW
	public String brokerURI = "tcp://iot.eclipse.org:1883";
	public String brokerUsername = "";
	public String brokerPassword = "";
	public int qos = 1;
	public String clientID = "SERG/RELAY/THERMAL/BLOB_COUNT";
	public String sensorPrefix = "SERG/THERMAL/BLOB_COUNT/ID/";
	
	//MySQL URI, USN + PW
	public String myURI = "jdbc:mysql://fallback-uri:3306/iottech_localevents";
	public String myUsername = "fallBackUser";
	public String myPassword ="fallbackPassword";
    
    
    public ConfigurationManager()
    {
     
        Path source = Paths.get("Relay_Conf.xml");
        
        //Test if the current conf exists, if not create from default
        if(Files.exists(source)){
        
            //Read the XML content
            try{
                readXML("Relay_Conf.xml");
                
            } catch(Exception e ){
                System.err.println("ConfManager Error, readConfFile - readAllBytes: " + e);
            }
            
        }else{
            //Write out a example file
            writeDefaultConfFile("Relay_Conf.xml");            
        }
                
    }

    //Get the name of a conf element via reflection
    public String getConfigurationValue(String namedElement)
    {
        String retVal = "";
        
        try{
            
         Field field = this.getClass().getDeclaredField(namedElement);
         field.setAccessible(true);
         
        return (String) field.get(this);
     
          
        } catch(Exception e){
            System.err.println("ConfManager Error, getConfigurationValue - Reflection: " + e);
            
        }
    
        return retVal;
    }
    
    //Read the XML file contents to Strings
    private boolean readXML(String xml) {
        
        try {
         
            // Make an  instance of the DocumentBuilderFactory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(xml);

            Element doc = dom.getDocumentElement();

            
            threadInterval = Long.parseLong(getTextValue(String.valueOf(threadInterval), doc, "threadInterval"));
            brokerURI = getTextValue(brokerURI, doc, "brokerURI");
            brokerUsername = getTextValue(brokerUsername, doc, "brokerUsername");
            brokerPassword = getTextValue(brokerPassword, doc, "brokerPassword");
            qos = Integer.parseInt(getTextValue(String.valueOf(qos), doc, "qos"));
            clientID = getTextValue(clientID, doc, "clientID");
            sensorPrefix = getTextValue(sensorPrefix, doc, "sensorPrefix");
            myURI = getTextValue(myURI , doc , "myURI");
            myUsername = getTextValue(myUsername , doc , "myUsername");
            myPassword = getTextValue(myPassword , doc , "myPassword");
            
            return true;

        } catch (Exception e) {
            System.err.println("ConfManager Error, readXML: " + e);
        }
        
        return false;
    }

    //Write the default conf file 
    private void writeDefaultConfFile(String xml) {
        Document dom;
        Element element = null;

        
        try {
            
            // instance of a DocumentBuilderFactory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element
            Element rootEle = dom.createElement("confVisionmMQTT");

            element = dom.createElement("threadInterval");
            element.appendChild(dom.createTextNode(String.valueOf(threadInterval)));
            rootEle.appendChild(element);
            
            // create data elements and place them under root    
            element = dom.createElement("brokerURI");
            element.appendChild(dom.createTextNode(brokerURI));
            rootEle.appendChild(element);

            element = dom.createElement("brokerUsername");
            element.appendChild(dom.createTextNode(brokerUsername));
            rootEle.appendChild(element);

            element = dom.createElement("brokerPassword");
            element.appendChild(dom.createTextNode(brokerPassword));
            rootEle.appendChild(element);

            element = dom.createElement("qos");
            element.appendChild(dom.createTextNode(String.valueOf(qos)));
            rootEle.appendChild(element);

            element = dom.createElement("clientID");
            element.appendChild(dom.createTextNode(clientID));
            rootEle.appendChild(element);

            element = dom.createElement("sensorPrefix");
            element.appendChild(dom.createTextNode(sensorPrefix));
            rootEle.appendChild(element);

            element = dom.createElement("myURI");
            element.appendChild(dom.createTextNode(myURI));
            rootEle.appendChild(element);
            
            element = dom.createElement("myUsername");
            element.appendChild(dom.createTextNode(myUsername));
            rootEle.appendChild(element);
            
            element = dom.createElement("myPassword");
            element.appendChild(dom.createTextNode(myPassword));
            rootEle.appendChild(element);
                    
            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(xml)));

            } catch (Exception e) {
                System.err.println("ConfManager Error, writeXML - Transformer: " + e);
            }

            } catch (Exception e) {
                System.err.println("ConfManager Error, writeXML - DocumentBuilder: " + e);
            }
        }
    
    //Get a  text value from the XML NodeList
    private String getTextValue(String def, Element doc, String tag) {
        String value = def;
        NodeList nl = doc.getElementsByTagName(tag);
        
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        
        return value;
    }

    
}
