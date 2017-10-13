package com.github.ontruck;

import com.github.ontruck.filters.FilterManager;
import com.github.ontruck.filters.GenericFilter;

import java.io.IOException;
import java.net.SocketException;

public class OnTruck implements Runnable {

	// Port number for socket
	private static final int UDP_PORT = 8721;
	private static final int TCP_PORT = UDP_PORT;

	private UDPConnection udpConnection;
	private TCPConnection tcpConnection;
	private FilterManager filterManager;
	private Driver driver;
	private DeadMansSwitch deadMansSwitch;


	public void init() {

		this.filterManager = new FilterManager(MopedState.Manual);

		try {
			driver = new Driver();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1); // Exit application if socket couldn't create socket
		}

		GenericFilter manualFilter = new GenericFilter(driver, MopedState.Manual);
		manualFilter.setState(MopedState.Manual);
		ManualController manualController = new ManualController(manualFilter);
		this.filterManager.addFilter(manualFilter);
		//deadMansSwitch = new DeadMansSwitch(manualFilter);

		try {
			// Create new socket
			udpConnection = new UDPConnection(UDP_PORT);
		} catch (SocketException e) {
			System.err.printf("Could not open socket on port %d:\n%s\n", UDP_PORT, e.getMessage());
			System.exit(-1); // Exit application if socket couldn't create socket
		}

		// Add a data processor for driving
		udpConnection.addDataProcessor(manualController::processEvent);
		//udpConnection.addDataProcessor((a,b,c) -> deadMansSwitch.ping());


		tcpConnection = new TCPConnection(TCP_PORT);
		//tcpConnection.addDataProcessor((m) -> deadMansSwitch.ping());
		tcpConnection.addDataProcessor(filterManager::processStateEvent);
	}

	public void doFunction() throws InterruptedException{
		// Random blocking call
		try {
			System.out.printf("Press enter to quit.\n");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void run() {
		init();

		// Start threads
		udpConnection.start();
		//deadMansSwitch.start();
		tcpConnection.start();

		try {
			doFunction();
		} catch (InterruptedException e) {
			System.out.println("**************** Interrupted.");
			return;
		}
	}

}
