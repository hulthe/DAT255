package com.github.ontruck;

import java.io.IOException;
import java.net.SocketException;

public class OnTruck {

	// Port number for socket
	private static final int UDP_PORT = 8721;
	private static final int TCP_PORT = UDP_PORT;

	private UDPConnection udpConnection;
	private TCPConnection tcpConnection;
	private DriveProtocol driver;
	private DeadMansSwitch deadMansSwitch;

    public static void main(String[] args) {
		OnTruck plugin = new OnTruck();
		plugin.run();
	}

	public void init() {

		try {
			driver = new DriveProtocol();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1); // Exit application if socket couldn't create socket
		}

		deadMansSwitch = new DeadMansSwitch(driver);

		try {
			// Create new socket
			udpConnection = new UDPConnection(UDP_PORT);
		} catch (SocketException e) {
			System.err.printf("Could not open socket on port %d:\n%s\n", UDP_PORT, e.getMessage());
			System.exit(-1); // Exit application if socket couldn't create socket
		}

		// Add a data processor for driving
		udpConnection.addDataProcessor(driver::processEvent);
		udpConnection.addDataProcessor((a,b,c) -> deadMansSwitch.ping());


		tcpConnection = new TCPConnection(TCP_PORT);
		tcpConnection.addDataProcessor(driver::processEvent);
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

    public void run() {
		init();

		// Start threads
		udpConnection.start();
		deadMansSwitch.start();
		tcpConnection.start();

		try {
			doFunction();
		} catch (InterruptedException e) {
			System.out.println("**************** Interrupted.");
			return;
		}
	}

}
