package serg.thermal.mqtt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import serg.thermal.mqtt.conf.ConfigurationManager;
import serg.thermal.mqtt.connectors.MQTTConnector;
import serg.thermal.mqtt.connectors.ProcessedVisionDB;
import serg.thermal.mqtt.pojo.ThermalRecord;

public class Main {

	public static void main(String[] args) {
		
		//Set the thread interval
		final long threadInterval;
		
		//A hashmap containing room occupancy values 
		//for each sensor ID as sent by MQTT
		Map<String, String> roomValues = new HashMap<String, String>();
		
		//Create a Conf manager and load/crate a file
		ConfigurationManager confMan = new ConfigurationManager();
		
		//Construct connector objects with info from config file
	
		
		final ProcessedVisionDB visionDB = new ProcessedVisionDB(
				confMan.myURI,
				confMan.myUsername,
				confMan.myPassword);
			
		
		final MQTTConnector mqConn = new MQTTConnector(
				confMan.brokerURI, 
				confMan.brokerUsername, 
				confMan.brokerPassword, 
				confMan.qos , 
				confMan.clientID, 
				confMan.sensorPrefix);
    	
 
		threadInterval = confMan.threadInterval;
		
		//Create relay thread
		 new Thread(    new Runnable() 
		    {
		        public void run() 
		        {
		        	
		        	//Keep the thread alive forever
	        		while(true){
		        		
			    		//A flag to show if a message should be sent in the comparison loop
			        	Boolean sendReport = false;
			    		
						//GET DB VALUES LOOP
			        	ArrayList<ThermalRecord> thermalRecords =  visionDB.getRecords();
			        	
			        	//Loop through thermal records
			        	for(ThermalRecord tRec : thermalRecords){
			        	
			        		//REPORT BOOL IS FALSE
				        	sendReport = false;
			        		
			        		//If the is a record in th e HM then check if it has changed
			        		if(roomValues.containsKey(tRec.sensorID)){
	
			        			//If the hashmap value is different then update and send a report
			        			if(!roomValues.get(tRec.sensorID).contentEquals(tRec.occupantCount)){
	
			        				roomValues.put(tRec.sensorID, tRec.occupantCount);
			        				sendReport = true;
			        			}
			        			
			        		}else{
			        			
			        			//No record in the HM  store it and send
			        			roomValues.put(tRec.sensorID, tRec.occupantCount);
			        			
			        			//Set send report value
			        			sendReport = true;
			        		}
			        		
			        	
			        		//IF REPORT TRUE THEN SEND the report in a new  thread
			        		if(sendReport){
			        	
			        			mqConn.sendSensorCountMessage(tRec.sensorID, tRec.occupantCount);
        						  	
			        			System.out.println("s");
			        			/*
			        			
			        			Old code, can't use due to socket with paho restrictions, must use 2 shared obj, slower (RR doesnt work either)
			        			
			        			new Thread(    new Runnable() 
			        		    {
			        		        public void run() 
			        		        {
			        		        	MQTTConnector mqConn = new MQTTConnector(
			        		    				confMan.brokerURI, 
			        		    				confMan.brokerUsername, 
			        		    				confMan.brokerPassword, 
			        		    				confMan.qos , 
			        		    				confMan.clientID, 
			        		    				confMan.sensorPrefix);
			        		        	
			        		        	mqConn.sendSensorCountMessage(tRec.sensorID, tRec.occupantCount);
			        		        	mqConn = null;
			        		        	
			        		        }    
			        		    }).start();
			        		    */
			        		}
			        		
			        	}
			        	
			        	thermalRecords = null;
			        	sendReport = null;
			        	System.gc();
			        
	
						try{
							Thread.sleep(threadInterval);
						}catch(Exception e){
							System.err.println("Thread pause error " + e);
						}
		        	
		        	}
		        }
		    }).start();
		
		

	}

}
