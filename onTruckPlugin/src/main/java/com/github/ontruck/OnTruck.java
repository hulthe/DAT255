package com.github.ontruck;

import com.github.moped.jcan.CAN;
import com.github.ontruck.controller.AutonomousController;
import com.github.ontruck.controller.DeadMansSwitch;
import com.github.ontruck.controller.ManualController;
import com.github.ontruck.controller.plan.PlanExecutor;
import com.github.ontruck.moped.Driver;
import com.github.ontruck.moped.SensorDataCollector;
import com.github.ontruck.network.TCPConnection;
import com.github.ontruck.network.UDPConnection;
import com.github.ontruck.states.FilterManager;
import com.github.ontruck.states.MopedState;
import com.github.ontruck.states.filters.*;

import java.io.IOException;
import java.net.SocketException;

public class OnTruck implements Runnable {

	// Port number for socket
	private static final int UDP_PORT = 8721;
	private static final int TCP_PORT = UDP_PORT;

	private UDPConnection udpConnection;
	private TCPConnection tcpConnection;

	private Driver driver;

	private FilterManager filterManager;

	private ManualController manualController;
	private DeadMansSwitch deadMansSwitch;

	private SensorDataCollector sensorDataCollector;
	private DistanceSensor distanceSensor;

	private AutonomousController autonomousController;
	private PlanExecutor autonomousPlanExecutor;


	public void init() {

		// Use FilterManager to collectively set the state of every StateFilter
		this.filterManager = new FilterManager(MopedState.Manual);

		// Driver talks to the CAN-bus and drives the car.
		try {
			String canInterface = System.getenv("CAN_INTERFACE");
			CAN can = new CAN(canInterface);
			driver = new Driver(can);
			sensorDataCollector = new SensorDataCollector(can);
		} catch (IOException e) {
			e.printStackTrace();
			exit(-1); // Exit application if socket couldn't create socket
		}

		{	// Set up filters & controllers
			/*
				**************                       **********     **********
				* Controller * --control commands--> * Filter * --> * Driver * --> CAN
				**************                       **********     **********
			 */
			ManualFilter manualFilter = new ManualFilter(driver);
			manualController = new ManualController(manualFilter);
			filterManager.addFilter(manualFilter);

			DMSFilter dmsFilter = new DMSFilter(driver);
			deadMansSwitch = new DeadMansSwitch(dmsFilter);
			filterManager.addFilter(dmsFilter);

			AutonomousFilter autoFilter = new AutonomousFilter(driver);
			autonomousPlanExecutor = new PlanExecutor(autoFilter);
			filterManager.addFilter(autoFilter);

			filterManager.setState(MopedState.Manual);
		}

		try {
			// Create new socket
			udpConnection = new UDPConnection(UDP_PORT);
		} catch (SocketException e) {
			System.err.printf("Could not open socket on port %d:%n%s%n", UDP_PORT, e.getMessage());
			System.exit(-1); // Exit application if socket couldn't create socket
		}

		// Add data processors for drive control
		udpConnection.addDataProcessor(manualController::processEvent);
		udpConnection.addDataProcessor((a,b,c) -> deadMansSwitch.ping());

		// Add data processors for state control
		tcpConnection = new TCPConnection(TCP_PORT);
		tcpConnection.addDataProcessor((m) -> deadMansSwitch.ping());
		tcpConnection.addDataProcessor(filterManager::processStateEvent);

		distanceSensor = new DistanceSensor();
		sensorDataCollector.addDataProcessor(distanceSensor::process);

		// create and start AI controller with executor
		autonomousController = new AutonomousController(distanceSensor, autonomousPlanExecutor);
	}

	public void doFunction() throws InterruptedException{
		// Random blocking call
		try {
			System.out.printf("Press enter to quit.%n");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startThreads() {
		udpConnection.start();
		tcpConnection.start();
		deadMansSwitch.start();

		sensorDataCollector.start();
		autonomousPlanExecutor.start();
		autonomousController.start();
	}

	private int stopThreads() {
		udpConnection.interrupt();
		tcpConnection.interrupt();
		deadMansSwitch.interrupt();
		sensorDataCollector.interrupt();
		autonomousPlanExecutor.interrupt();
		autonomousController.interrupt();

		try {
			udpConnection.join();
			//tcpConnection.join();
			deadMansSwitch.join();
			sensorDataCollector.join();
			autonomousPlanExecutor.join();
			autonomousController.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	@Override
    public void run() {
		init();

		startThreads();

		try {
			doFunction();
		} catch (InterruptedException e) {
			System.out.println("**************** Interrupted.");
			return;
		}

		exit(stopThreads());
	}

	private void exit(int status) {
		System.exit(status);
	}

	public static void main(String[] args) {
		OnTruck onTruck = new OnTruck();
		onTruck.run();
	}
}
