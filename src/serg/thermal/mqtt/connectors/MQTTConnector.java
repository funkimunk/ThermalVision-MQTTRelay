package serg.thermal.mqtt.connectors;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MQTTConnector {

	//The broker URI
	private String brokerURI = null;

	//The Usernams for the broker
	private String brokerUsn = null;
	
	//The Password for the broker
	private String brokerPw = null;
	
	//The ID of this client
	private String clientID = null;
	
	//The QOS value
	private int qos	= -1;
	
	//The prefix for the sensor environment
	private String sensorPrefix = "SERG/THERMAL/BLOB_COUNT";
    
	//The memory persistence object  
    MemoryPersistence persistence = null;
    

    
    
    //Constructor with values
    public MQTTConnector(String iBrokerURI, 
    		String iBrokerUsn, 
    		String iBrokerPw ,
    		int iQos, 
    		String iClientID,
    		String iSensorPrefix){
    	
    	persistence = new MemoryPersistence();
    	
    	//Set broker params
    	brokerURI = iBrokerURI;
    	brokerUsn = iBrokerUsn;
    	brokerPw = iBrokerPw;
    	
    	//Set QOS
    	qos = iQos;
    	
    	//Set Client ID
    	clientID = iClientID;
    	
    	//Set the sensor prefix
    	sensorPrefix = iSensorPrefix;
    }
    
    
    public void sendSensorCountMessage(String sensorID, String roomCount){
    	
    	try {
    		
    		//Build MQTT CLIENT
            MqttClient mqttClient = new MqttClient(brokerURI, clientID, persistence);
            
            //Get connection options and set as clean
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            //System.out.println("Connecting to broker: "+brokerURI);
            
            //If a Pw/Usn has been supplied
            if(brokerUsn.trim().length()> 0 && 
        		brokerPw.trim().length() > 0){
            	
            	//Set password param
            	
            }
            
            
            //Connect to broker
            mqttClient.connect(connOpts);
            //System.out.println("Connected");
            //System.out.println("Publishing message: "+roomCount);
            
            //Crate message 
            MqttMessage mqttMessage = new MqttMessage(roomCount.getBytes());
            
            //Set QOS level
            mqttMessage.setQos(qos);
            
            //Publish message
            mqttClient.publish(sensorPrefix + sensorID, mqttMessage);
            //System.out.println("Message published");
            
            //Disconnect
            mqttClient.disconnect();
            //System.out.println("Disconnected");
            
            //Cleanup
            mqttClient = null;
            connOpts = null;
            mqttMessage = null;
            System.gc();
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    
    
    
    
    
	
	
	
}
