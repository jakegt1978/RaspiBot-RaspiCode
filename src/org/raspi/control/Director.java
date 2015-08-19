package org.raspi.control;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInputInt;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelOutputInt;
import org.jcsp.lang.Guard;

/**
 * 
 * @author SkynetBase
 *	
 *	CONSTANTS (Defined in NXT component):
 *	STOP 	= 0;	HALT	= 1;	OBSTCL 	= 2;	NORM 	= 3;
 *	FORWARD = 4;	LEFT 	= 5;	RIGHT 	= 6;	BACK 	= 7;
 */	

public class Director implements CSProcess{
	
  //system control contants - (nxt commands)		// Commands scanned from barcodes (qr or code_128)

	private int target = -973275;
	

	private static final int QUIT 		=  0;		private static final int CMD_QUIT			= 987654321;
	private static final int HALT		=  1;		private static final int CMD_HALT			= 222222;		
  //private static final int OBSTCL 	=  2;
  //private static final int NORM 		=  3;
	private static final int FORWARD 	=  4;		private static final int CMD_FORWARD		= 100004;
	private static final int LEFT 		=  5;		private static final int CMD_TURNLEFT		= 100005; 
	private static final int RIGHT 		=  6;		private static final int CMD_TURNRIGH		= 100006;
	private static final int BACK 		=  7;		private static final int CMD_BACK			= 100007;
  //private static final int ERROR 		=  8;		
	private static final int FW30	 	=  10;		private static final int CMD_FW30			= 100010;
	private static final int BK30	 	=  11;		private static final int CMD_BK30			= 100011;
	private static final int LEFT90 	=  12;		private static final int CMD_LEFT90			= 100012;
	private static final int RIGHT90 	=  13;		private static final int CMD_RIGHT90		= 100013;
	private static final int LEFT180 	=  14;		private static final int CMD_180LEFT 		= 100014;	
	private static final int RIGHT180	=  15;		private static final int CMD_180RIGHT 		= 100015;

		
	// mill-sec to wait before scanning for barcode
	private static final int CMD_INITW8			= 10000; //ms											
	private int 			 CMD_CONTSCAN		= 0; 	 //ms (continue straight away)
	
	//comm channels
	private ChannelInputInt extraIn;
	private ChannelOutputInt outCh;
	private ChannelOutputInt outConsole;
	//comm channels
	private AltingChannelInputInt mergerIn, inConsole;

    private boolean running = false;
	private boolean handled = false;
	private int cliNo		= 3;
	
	//debug vars
	private int counter = 0; // loop-counter
	private static final int CMD_START			= 1234567; // start the robot

	public Director(AltingChannelInputInt mergerIn, AltingChannelInputInt inConsole, ChannelOutputInt outCh, ChannelOutputInt outConsole, int cliNo, ChannelInputInt extraIn){
    	this.mergerIn	= mergerIn;
    	this.inConsole	= inConsole;
    	this.outCh		= outCh;
    	this.outConsole = outConsole;
    	this.cliNo		= cliNo + 1;
    	this.extraIn	= extraIn;
	}
	
	@Override
	public void run() {
		
		/**
		 *  wait for phones & nxt to connect
		  * before progressing 					
		  * 
		  * */
		for (int i = 0; i < cliNo; i++)
		{
			int temp = extraIn.read();
			outCh.write(CMD_INITW8);
			System.out.println("director-waiting for android or nxt: " + temp + ": i: " + i + " No of Clients: " + cliNo);
		}

		/** it's safe to assume we are now running */
		this.running = true;
		
		final Guard[] altChans = {mergerIn, inConsole};
		final Alternative alt = new Alternative (altChans);
	
		int inPut=-1;
		
		while(running){
			
			/** OUTTER SWICH-CASE */
			switch(alt.priSelect()){
			
			/** INNER SWICH-CASE: Input from bar-code Sensor */
			case 0:
				
				//get data (from barcode-sensor)
				inPut = mergerIn.read();
	
				/*	INNER SWICH-CASE (deals with bar-code sensor input) */
				switch( inPut ){
	
					//restart nxt
					case CMD_QUIT:
						outCh.write(CMD_CONTSCAN);
						//act as client
						outCh.write(QUIT);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_HALT:
						outCh.write(CMD_CONTSCAN);
						//act as client - send halt command
						outCh.write(HALT);
						inPut = extraIn.read();
						handled = true;
						break;
				
					//start command
					case CMD_START:
						outCh.write(CMD_CONTSCAN);
						//act as client - move forward - until an event occurs/stopped
						outCh.write(FORWARD);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_FORWARD:
						outCh.write(CMD_CONTSCAN);
						//act as client - move forward - until an event occurs/stopped
						outCh.write(FORWARD);
						inPut = extraIn.read();
						handled = true;
						break;
					
					case CMD_TURNLEFT:
						outCh.write(CMD_CONTSCAN);
						//act as client - turn through left-axis - until stopped
						outCh.write(LEFT);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_TURNRIGH:
						outCh.write(CMD_CONTSCAN);
						//act as client - turn through left-axis - until stopped
						outCh.write(RIGHT);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_BACK:
						outCh.write(CMD_CONTSCAN);
						//act as client - move back -  until stopped
						outCh.write(BACK);
						inPut = extraIn.read();
						handled = true;
						break;
		
					case CMD_LEFT90:
						outCh.write(CMD_CONTSCAN);
						//act as client - turn 90 through left-axis
						outCh.write(LEFT90);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_RIGHT90:
						outCh.write(CMD_CONTSCAN);
						//act as client - turn 90 through right-axis
						outCh.write(RIGHT90);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_180LEFT:
						outCh.write(CMD_CONTSCAN);
						//act as client - do 180 turn degrees through left-axis
						outCh.write(LEFT180);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_180RIGHT:
						outCh.write(CMD_CONTSCAN);
						//act as client - do 180 turn degrees through right-axis
						outCh.write(RIGHT180);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_FW30:
						outCh.write(CMD_CONTSCAN);
						//act as client - move forward 30 cm
						outCh.write(FW30);
						inPut = extraIn.read();
						handled = true;
						break;
						
					case CMD_BK30:
						outCh.write(CMD_CONTSCAN);
						//act as client - move back 30cm
						outCh.write(BK30);
						inPut = extraIn.read();
						handled = true;
						break;					
				}//close switch
				
				/** it could have been the target: */
				if(inPut == target){
					System.out.println("BOOM: TARGET FOUND");
					outCh.write(CMD_CONTSCAN);
					outCh.write(HALT);
					extraIn.read();
				}
				/** it was a bar-code that doesn't contain a command
				*   and it wasn't our target */
				else if(handled == false && inPut != target){
					//just continue scanning
					outCh.write(CMD_CONTSCAN);
				}
				break;
			
			
			/** OUTTER SWICH-CASE: Console input */
			case 1:
				int temp = inConsole.read();
				
				//if it was a directional command: pass it to the NXT
				if(temp == CMD_FORWARD)
				{
					outCh.write(FORWARD);
					inPut = extraIn.read();
				}
				else if(temp == CMD_BACK)
				{
					outCh.write(BACK);
					inPut = extraIn.read();
				}
				else if(temp == CMD_TURNLEFT)
				{
					outCh.write(LEFT);
					inPut = extraIn.read();
				}
				else if(temp == CMD_TURNRIGH)
				{
					outCh.write(RIGHT);
					inPut = extraIn.read();
				}
				else if(temp == CMD_HALT)
				{
					outCh.write(HALT);
					inPut = extraIn.read();
				}
				
				/*	else Set target-barcode	*/
				else
				{
					this.target = temp;
				}
				//write confirmation
				outConsole.write(0);
			break;
				
			}//close outter switch
			System.out.println("input: " + inPut + ": i: " + counter + " ");
			handled = false;
			counter ++;
		}//close while()
	}//close run()
}//close class
