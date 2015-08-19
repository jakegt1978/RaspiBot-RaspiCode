package org.raspi.control;

/** 
 * Excuse the commented code..
 *  it is there to facilitate the switch to additional (android) bar-code sensors
 *  As well as illustrating to anyone who reads this, how it can be (easily) achieved.
 *
 */


import org.jcsp.lang.*;

public class BTMergeServer implements CSProcess{

	private AltingChannelInputInt androidIn, nxtIn, cmdInpt;
	private ChannelInputInt opr;
	private ChannelOutputInt toDirector, extraOut, androidOut, nxtOut;
	private boolean running = false;
	private boolean androidFirstTime = true;
	private boolean nxtFirstTime = true;
	
	  /** Constructor */
	public BTMergeServer (AltingChannelInputInt androidIn, AltingChannelInputInt nxtIn, AltingChannelInputInt cmdInpt, ChannelOutputInt toDirector, ChannelInputInt opr, ChannelOutputInt androidOut, ChannelOutputInt nxtOut, ChannelOutputInt extraOut) {
		this.androidIn	= androidIn;
		this.nxtIn		= nxtIn;
		this.cmdInpt	= cmdInpt;
    	this.toDirector	= toDirector;
    	this.opr		= opr;
    	this.androidOut	= androidOut;
    	this.nxtOut		= nxtOut;
    	this.extraOut	= extraOut;
    }
	@Override
	public void run() {
		this.running = true;
		final Guard[] altChans = {androidIn, nxtIn, cmdInpt};
		final Alternative alt = new Alternative (altChans);
		boolean preCon[] = {true, true, true};
		int operation = 0;
		int temp = 0;
		
		while(running == true){
			switch(alt.priSelect(preCon))
			{
				//(android) bar-code sensor input
				case 0:
					if(androidFirstTime){
						this.extraOut.write(androidIn.read());
						androidOut.write(opr.read());
						androidFirstTime = false;
					}else{
						System.out.println("CASE 0:");
						
						//read input from droid
						temp = androidIn.read();
						
						//output the data and
						//get a command
						toDirector.write(temp);
						operation = opr.read();
						
						//return command to 'android'
						androidOut.write(operation);
					}
					break;

				//NXT-INPUT
				case 1:
					if(nxtFirstTime){
						this.extraOut.write(nxtIn.read());
						this.nxtOut.write(this.opr.read());
					}else{
						System.out.println("CASE 1:");
						
						//read input from nxt
						temp = nxtIn.read();
						
						//output the data and
						//get a command
						toDirector.write(temp);
						operation = opr.read();
						
						//return command to 'android'
						nxtOut.write(operation);
					}
					break;
				
				//route-command from director to nxt
				case 2:
					System.out.println("CASE 2:");
					
					//read input from direcor process
					temp = this.cmdInpt.read();
					
					//send command && read response
					this.nxtOut.write(temp);
					int response = nxtIn.read();

					//return command to 'Director'
					extraOut.write(response);
					
					break;
					
			}//close switch-case
		}//close while()
	}//close run()
}//close class
