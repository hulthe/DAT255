package plugins;

import java.io.IOException;
import com.sun.squawk.VM;
import sics.port.PluginPPort;
import sics.port.PluginRPort;
import sics.plugin.PlugInComponent;

public class onTruck extends PlugInComponent {
    //private PluginPPort fs;
    //private PluginRPort ff;
	
    public onTruck() {}
	
    public onTruck(String[] args) {
	super(args);
    }
	
    public static void main(String[] args) {
	onTruck plugin = new onTruck(args);
	plugin.run();
    }

    public void init() {
	//fs = new PluginPPort(this, "fs");
	//ff = new PluginRPort(this, "ff");
    }
	
    public void doFunction() throws InterruptedExceptiong
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
