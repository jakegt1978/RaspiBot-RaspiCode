package org.raspi.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelOutputInt;

import lejos.pc.comm.NXTConnector;

public class NXT_Bluetooth_Server implements CSProcess{
	private DataInputStream  dis;
	private DataOutputStream dos;
	private ChannelInputInt inCh;
	private ChannelOutputInt outCh;
	private final int NXT_STRT = 81818;
	private boolean running = false;
	
	/** constructor */
	public NXT_Bluetooth_Server(ChannelInputInt inCh, ChannelOutputInt outCh) {
    	this.inCh	= inCh;
    	this.outCh	= outCh;
	}
	
	/** run */
	public void run(){
		connect();
		
		//broadcast "ready"
		outCh.write(NXT_STRT);
		inCh.read();
		System.err.println("nxt ready");
		
		//read the response
		writeBTCmd(4);		
		int cmd = readBTCmd();
		System.err.println("nxt com-check done!!");

		while(running){
			
			//read raspi instruction
			cmd = inCh.read();

			System.err.println("cmd read:" + cmd);
			//pass onto nxt
			//read the response
			writeBTCmd(cmd);
			System.err.println("nxt write!!");
			cmd = readBTCmd();
			System.err.println("nxt read!!");

			
			//write out nxt response
			outCh.write(cmd);
			
		}//close while
	}

    /** connect to LEGO_NXT */
	private void connect(){
		
		NXTConnector conn 	= new NXTConnector();		

		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");
		
		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		dos	= new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());

		System.err.println("success!!");
		running = true;
	}
	
	/** write a command to the lego-nxt */
	private void writeBTCmd(int cmd){
		System.err.println("before cmd write: " +cmd);
		try {	
				dos.writeInt(cmd); 
				dos.flush();	
			} catch (IOException e) 
			{	
				System.err.println("Failed to write to NXT");	
				System.exit(1);	
			}
	}

	/** read a command From the lego-nxt */
	private int readBTCmd(){
		int temp = -1;
		try {	temp = dis.readInt();	} catch (IOException e) {	System.err.println("Failed to write to NXT");	System.exit(1);	}
		return temp;
	}
	
}
