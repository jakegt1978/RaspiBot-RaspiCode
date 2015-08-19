package org.raspi.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import javax.microedition.io.StreamConnection;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelOutputInt;

public class Barcode_Sensor_BluetoothServer implements CSProcess{

	private boolean connected = false;
	private StreamConnection connection;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ChannelInputInt inCh;
	private ChannelOutputInt outCh;
	private int temp, oper;
	private final int CONF_STRT = 8000;
	private final int REQ_STRT  = 9999;
	
	  /** Constructor */
    public Barcode_Sensor_BluetoothServer(ChannelInputInt inCh, ChannelOutputInt outCh, StreamConnection connection) {
    	this.inCh	= inCh;
    	this.outCh	= outCh;
    	this.connection = connection;
    }

    @Override
    public void run() {
    	//start conn & wait for the 'start' command
    	connect();
    	//get first command
    	outCh.write(REQ_STRT);
        oper = inCh.read();
        

        while(connected() == true)
        {
        	/** skip this if first run */
            temp = readResult(oper);
            System.out.println("  opp: " + oper);
            
        	if(temp != CONF_STRT)
        	{
	        	//write the barcode input - to a par process
	        	outCh.write(temp);
	        	oper = inCh.read();
        	}
        }//close while
        
    }//close run
    
    private boolean connected(){
    	return this.connected;
    }
   
    private void connect(){

        // waiting for connection
        while(connected == false) {
            try {
                System.out.println("hmm...");
                dis = new DataInputStream(connection.openDataInputStream());
                dos = new DataOutputStream(connection.openDataOutputStream());
                connected = true;	                
                System.out.println("Done waiting...");
            } catch (Exception e) {
            	System.out.println("EXCEPTION!...");
            	connected = false;
            }
        }        
    }//close connect()	
    
    private int readResult(int opr){
    	int temp;
		try {
			temp = dis.readInt();
			dos.writeInt(opr);
		} catch (IOException e) {
			connected = false;
			return 919191;
		}
    	return temp;
    }
}
