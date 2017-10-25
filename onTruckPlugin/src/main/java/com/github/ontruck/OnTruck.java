package com.github.ontruck;

import com.github.moped.jcan.CAN;
import com.github.ontruck.controller.AutonomousController;
import com.github.ontruck.controller.DeadMansSwitch;
import com.github.ontruck.controller.ManualController;
import com.github.ontruck.controller.plan.PlanExecutor;
import com.github.ontruck.moped.DistanceSensor;
import com.github.ontruck.moped.Driver;
import com.github.ontruck.moped.IDistanceSensor;
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
	private CAN can;

	private Driver driver;

	private FilterManager filterManager;

	private ManualController manualController;
	private DeadMansSwitch deadMansSwitch;

	private SensorDataCollector sensorDataCollector;
	private IDistanceSensor distanceSensor;

	private AutonomousController autonomousController;
	private PlanExecutor autonomousPlanExecutor;

	private OnTruck() {
		init();
	}

	private void init() {

		// Driver talks to the CAN-bus and drives the car.
		try {
			String canInterface = System.getenv("CAN_INTERFACE");
			can = new CAN(canInterface);
			driver = new Driver(can);
		} catch (IOException e) {
			e.printStackTrace();
			exit(-1); // Exit application if socket couldn't create socket
		}

		{ // Networking
			// Create and setup new UDPConnection
			try {
				udpConnection = new UDPConnection(UDP_PORT);
			} catch (SocketException e) {
				System.err.printf("Could not open socket on port %d:%n%s%n", UDP_PORT, e.getMessage());
				exit(-1); // Exit application if socket couldn't create socket
			}

			// Add data processors for state control
			tcpConnection = new TCPConnection(TCP_PORT);
		}



		// Setup filter logic
		{
			// Create FilterManager
			this.filterManager = new FilterManager(MopedState.Manual);

			// Set up filters & controllers
			/*
				**************                       **********     **********
				* Controller * --control commands--> * Filter * --> * Driver * --> CAN
				**************                       **********     **********
			 */

			// Create and setup ManualController
			ManualFilter manualFilter = new ManualFilter(driver);
			manualController = new ManualController(manualFilter);
			udpConnection.addDataProcessor(manualController::processEvent);
			filterManager.addFilter(manualFilter);

			// Create and setup DeadMansSwitch Controller
			DMSFilter dmsFilter = new DMSFilter(driver);
			deadMansSwitch = new DeadMansSwitch(dmsFilter);
			udpConnection.addDataProcessor((a,b,c) -> deadMansSwitch.ping());
			filterManager.addFilter(dmsFilter);

			// Create and setup AutonomousFilter controller
			AutonomousFilter autoFilter = new AutonomousFilter(driver);
			autonomousPlanExecutor = new PlanExecutor(autoFilter);
			autonomousController = new AutonomousController(distanceSensor, autonomousPlanExecutor);
			filterManager.addFilter(autoFilter);

			// Use FilterManager to collectively set the state of every StateFilter
			filterManager.setState(MopedState.Manual);
			tcpConnection.addDataProcessor(filterManager::processStateEvent);
		}

		sensorDataCollector = new SensorDataCollector(can);
		distanceSensor = new DistanceSensor();
		sensorDataCollector.addDataProcessor(distanceSensor::process);
	}

	private void startCLI() {
		// Blocks the thread until the user enter some input
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
		startThreads(); // Starts up application
		startCLI(); // Run the CLI
		exit(stopThreads()); // Upon exiting the CLI, stop the application and exit
	}

	private void exit(int status) {
		System.exit(status);
	}

	public static void main(String[] args) {
		OnTruck onTruck = new OnTruck();
		onTruck.run();
	}
}
