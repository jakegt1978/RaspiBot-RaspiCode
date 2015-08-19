package org.raspi.control;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;


import org.jcsp.lang.ChannelInt;
import org.jcsp.lang.One2OneChannelInt;
import org.jcsp.lang.Parallel;

@SuppressWarnings("deprecation")
public class RaspiMain {
	//command-line: compile (from inside src/org/raspi/control)
	//javac -cp .;jcsp.jar;pccomm.jar;pctools.jar;bluecove.jar RaspiMain.java BTMergeServer.java CSnxt_BTServer.java ParBTServ.java UserConsole.java Director.java

	//command-line: run (from src folder)
	//java -cp .;org/raspi/control/jcsp.jar;org/raspi/control/pccomm.jar;org/raspi/control/pctools.jar;org/raspi/control/bluecove.jar org/raspi/control/RaspiMain
	
	public static void main(String[] args) {
		One2OneChannelInt btms2android, android2btms, dirExtraIn, btms2drct, drct2btms, nxt2btms, btms2nxt, cons2Dir, dir2cons;
		StreamConnection connectionSide;
        StreamConnectionNotifier notifierSide;
        
		btms2android 	= ChannelInt.createOne2One();
		android2btms	= ChannelInt.createOne2One();
		btms2drct 		= ChannelInt.createOne2One();
		drct2btms 		= ChannelInt.createOne2One();
		nxt2btms 		= ChannelInt.createOne2One();
		btms2nxt 		= ChannelInt.createOne2One();
		cons2Dir 		= ChannelInt.createOne2One();
		dirExtraIn		= ChannelInt.createOne2One();
		dir2cons	 	= ChannelInt.createOne2One();
		LocalDevice local 	= null;
        connectionSide 		= null;


        
        /** connect to android 1 & 2 (possibly 3 & 4..) - exit if there is an error */
        try 
        {
        	
			local = LocalDevice.getLocalDevice();
	        local.setDiscoverable(DiscoveryAgent.GIAC);	
	        
	        System.out.println("bfore BT-CON: 1 - front bar-code sensor: htc");
	        String url = "btspp://localhost:" + "0000110100001000800000805AAAAAAA" + ";name=RemoteBluetooth1";	
	        /* An alternative UUID: "0000110100001000800000805F9B34AA"	*/
	        notifierSide = (StreamConnectionNotifier)Connector.open(url,3,false);
	        connectionSide = notifierSide.acceptAndOpen();
	        System.out.println("BT-CON: sensor COMPLETE");
  
        } catch (Exception e) {	System.out.println("BT-CON ERROR");	System.exit(0);	}

		Barcode_Sensor_BluetoothServer android = new Barcode_Sensor_BluetoothServer(	
														btms2android.in(), 
														android2btms.out(), 
														connectionSide
									  			);
		
		//NXT Bluetooth Process
		NXT_Bluetooth_Server cxs = new NXT_Bluetooth_Server 
				(	
					btms2nxt.in(),
					nxt2btms.out()
				);
		
		//Bluetooth input 'merger' process
		BTMergeServer btms = new BTMergeServer	
				(	
					android2btms.in(),
					nxt2btms.in(),
					drct2btms.in(),
					btms2drct.out(),
					drct2btms.in(),
					btms2android.out(),
					btms2nxt.out(),
					dirExtraIn.out()
				);
		
		//Director Process (with 1 android bar-code sensor
		Director tp = new Director
				(	
					btms2drct.in(), 
					cons2Dir.in(),
					drct2btms.out(),
					dir2cons.out(),
					1,
					dirExtraIn.in()
				);
		
		UserConsole cons = new UserConsole		
				(
					dir2cons.in(),
					cons2Dir.out()
				);
		

		
		//start the par-mage
		Parallel mp = new Parallel();
		mp.addProcess(android);
		mp.addProcess(btms);
		mp.addProcess(tp);
		mp.addProcess(cxs);
		mp.addProcess(cons);
		
		mp.run();
	}

}
