package org.raspi.control;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelOutputInt;
import java.io.Console;

public class UserConsole implements CSProcess {
	
	private static final int CMD_FORWARD		= 100004;
	private static final int CMD_TURNLEFT		= 100005; 
	private static final int CMD_TURNRIGH		= 100006;
	private static final int CMD_BACK			= 100007;
	private static final int CMD_HALT			= 222222;
	
	private ChannelInputInt inChan;
	private ChannelOutputInt outChan;
	private boolean running = false;
	private Console console;
	char[] passwd;

	public UserConsole(ChannelInputInt inChan, ChannelOutputInt outChan){
		this.inChan  = inChan;
		this.outChan = outChan;
		running = true;
		
		/**	ensure we have a console - (exit the application otherwise).	*/
		console = System.console();
		if(console == null)
		{
            System.err.println("\nNo console.");
            System.exit(1);			
		}
		
	}
	
	@Override
	public void run() {
		int trgt = -1;
		while(running){
			System.err.println("\nPlease enter target (integer only) or Directional CMD (W:S:A:D:H):");
			String target = "";
			try{
				target 	= console.readLine();
				trgt = Integer.parseInt(target);
				//set the target && get confirmation
				outChan.write(trgt);
				inChan.read();
				System.err.println("\nTarget has been defined (or cmd dispatched)!");
				}
			catch(Exception e){
						//nested try-catch
						try
						{
						if(target.equalsIgnoreCase("w"))
						{
							System.err.println("\n Forward (Start).");
							outChan.write(CMD_FORWARD);
							inChan.read();
						}
						else if(target.equalsIgnoreCase("S"))
						{
							System.err.println("\n Back.");
							outChan.write(CMD_BACK);
							inChan.read();
						}
						else if(target.equalsIgnoreCase("A"))
						{
							System.err.println("\n Left.");
							outChan.write(CMD_TURNLEFT);
							inChan.read();
						}
						else if(target.equalsIgnoreCase("D"))
						{
							System.err.println("\n Right.");
							outChan.write(CMD_TURNRIGH);
							inChan.read();
						}
						else if(target.equalsIgnoreCase("h"))
						{
							System.err.println("\n STOP");
							outChan.write(CMD_HALT);
							inChan.read();
						}
						}catch(Exception xe){
							System.err.println("\nIntegers (or cmd's) only please!");
						}//close nested try-catch
			}//close outer try-catch
		}//close while()
	}//close run()
}//close class
