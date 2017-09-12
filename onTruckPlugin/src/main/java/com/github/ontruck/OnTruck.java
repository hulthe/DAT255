package com.github.ontruck;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;

public class OnTruck extends PlugInComponent {
    //private PluginPPort fs;
    //private PluginRPort ff;
	
    public OnTruck() {}
	
    public OnTruck(String[] args) {
	super(args);
    }
	
    public static void main(String[] args) {
	OnTruck plugin = new OnTruck(args);
	plugin.run();
    }

    public void init() {
	//fs = new PluginPPort(this, "fs");
	//ff = new PluginRPort(this, "ff");
    }
	
    public void doFunction() throws InterruptedException{
    }

    private void interpretMessage(byte[] message){
		if (verifyMessage(message)){
			// ToDo
		}
	}

	private boolean verifyMessage(byte[] message){
    	// ToDo
		return true;
	}

    public void run() {
	init();

	try {
	    doFunction();
	} catch (InterruptedException e) {
	    VM.println("**************** Interrupted.");
	    return;
	}
    }
}
