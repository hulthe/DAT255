package com.github.ontruck.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class UDPConnection extends Thread {
	public 	static final byte POWER_OP_CODE = 0x50;
	public static final byte STEER_OP_CODE = 0x53;
	public static final byte BRAKE_OP_CODE = 0x42;

	private final List<DataProcessor> dataProcessors = new LinkedList<DataProcessor>();

	private final byte[] message = new byte[7];

	private final DatagramSocket socket;
	private final DatagramPacket packet= new DatagramPacket(message, message.length);

	public interface DataProcessor{
		void process(byte type, byte payload, byte stateGroup);
	}

	public UDPConnection(int port) throws SocketException{
		socket = new DatagramSocket(port);
		this.setDaemon(true); // Make sure thread closes when application does.
	}

	public boolean addDataProcessor(DataProcessor processor){
		synchronized (dataProcessors) {
			return dataProcessors.add(processor);
		}
	}

	public void run() {
		while (!isInterrupted()){ // Run until interrupted
			try {
				// Read and then process the message
				socket.receive(packet);
				process(message);

				// Clear buffer
				for(int i = 0; i < message.length; i++) { message[i] = 0; }
			}catch (IOException ex){
				System.err.printf("Error while reading from socket: %s%n", ex.getMessage());
			}
		}
		System.out.printf("UDP thread closed%n");
	}

	private void process(byte[] data) {
		if (validate(data)){
			DataProcessor[] ProcessorList;

			// Synchronized copy to avoid concurrency problems
			synchronized (dataProcessors){
				ProcessorList = dataProcessors.toArray(new DataProcessor[dataProcessors.size()]);
			}

			for (DataProcessor processor : ProcessorList) {
				processor.process(data[1], data[2], data[3]);
			}
		}
	}

	static boolean validate(byte[] data) {
		//Checks if starter, and then terminator is correct
		if (data[0] != 1) return false;
		if (data[6] != 4) return false;

		//makes sure the checksum is correct
		byte[] verificationHash = new byte[]{data[4], data[5]};
		byte[] checksum = checksum(data[1], data[2], data[3]);
		return verificationHash[0] == checksum[0] && verificationHash[1] == checksum[1];
	}

	private static byte[] checksum(byte type, byte payload, byte stateGroup) {
		byte[] bytes = new byte[]{type, payload, stateGroup};

		try {
			byte[] checksum = MessageDigest.getInstance("MD5").digest(bytes);
			return new byte[] {checksum[0], checksum[1]};
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return new byte[0];
		}
	}

	@Override
	public void interrupt(){
		this.socket.close(); // Close the socket before interrupt
		super.interrupt();
	}
}