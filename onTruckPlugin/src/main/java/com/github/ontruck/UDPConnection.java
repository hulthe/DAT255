package com.github.ontruck;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class UDPConnection extends Thread {
	private final List<DataProcessor> dataProcessors = new LinkedList<DataProcessor>();

	private final byte[] message = new byte[6];

	private final DatagramSocket socket;
	private final DatagramPacket packet= new DatagramPacket(message, message.length);

	public interface DataProcessor{
		void process(byte type, byte payload);
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
				System.err.printf("Error while reading from socket: %s \n", ex.getMessage());
			}
		}
		System.out.printf("UDP thread closed\n");
	}

	private void process(byte[] data) {
		if (validate(data)){
			DataProcessor[] ProcessorList;

			// Synchronized copy to avoid concurrency problems
			synchronized (dataProcessors){
				ProcessorList = dataProcessors.toArray(new DataProcessor[dataProcessors.size()]);
			}

			for (DataProcessor processor : ProcessorList) {
				processor.process(data[1], data[2]);
			}
		}
	}

	private boolean validate(byte[] data) {
		if (data[0] != 1) return false;
		if (data[5] != 4) return false;

		byte[] verificationHash = new byte[2];
		verificationHash[0] = data[3];
		verificationHash[1] = data[4];
		return (checksum(data[1], data[2]).equals(verificationHash));
	}

	private byte[] checksum(byte type, byte payload) {
		byte[] bytes = new byte[2];
		bytes[0] = type;
		bytes[1] = payload;

		try {
			return MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void interrupt(){
		this.socket.close(); // Close the socket before interrupt
		super.interrupt();
	}
}