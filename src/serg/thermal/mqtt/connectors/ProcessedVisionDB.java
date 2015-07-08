package serg.thermal.mqtt.connectors;

import java.util.ArrayList;

import serg.thermal.mqtt.pojo.ThermalRecord;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class ProcessedVisionDB {

	//MySQL connection info
	String myURI = null;
    String myUsername = null;
    String myPassword =null;
	   

     public ProcessedVisionDB(String iURI, String iUsr, String iPw){
        
    	 //Import the driver
	     try{
	         Class.forName("com.mysql.jdbc.Driver");
	     }catch(Exception e){
	         System.err.println("MySql Driver class not found");
	     }
         
	     //Set the params
	     myURI = iURI;
	     myUsername = iUsr;
	     myPassword = iPw;
    }
   
     
     
     public ArrayList<ThermalRecord> getRecords(){
    	    
    	 //Return object
         ArrayList<ThermalRecord> ret = new ArrayList<>();
        
         try {
        	 
        	 //New thermal record
        	 ThermalRecord tRec = new ThermalRecord();
             
             Connection con = null;
             Statement st = null;
             ResultSet rs = null;
             
             con = DriverManager.getConnection(myURI, myUsername, myPassword);
             st = con.createStatement();
             rs = st.executeQuery("SELECT SensorID, BlobCount FROM iottech_localevents.events GROUP BY SensorID");

             while (rs.next()) {
                 
            	 tRec = new ThermalRecord();
            	 tRec.sensorID = rs.getString(1);
            	 tRec.occupantCount = rs.getString(2);

                 ret.add(tRec);
                 
             }
             
             con.close();

         } catch (Exception ex) {
             System.err.println(ex);
         } 
         
         return ret;
     }
	
}
