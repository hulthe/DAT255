package com.github.ontruck.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class opens an TCP socket and sends received messages to a set of {@link DataProcessor}s.
 */
public class TCPConnection extends Thread {

	// Determines the end of a tcp message
	private static final char TERMINATOR = 0x04; // Default ASCI terminator

	private final int PORT;           //the port is set in the constructor
	private static final int TIMEOUT = 1000; //this is in ms and is the delay between pings

	private OutputWorker outputWorker = null;
	private InputWorker inputWorker = null;

	private Socket socket = null;

	// Code to be executed with received message
	private List<DataProcessor> dataProcessors = new LinkedList<>();

	/**
	 * This interface holds a process method to be called when the TCPConnection receives a message.
	 */
	public interface DataProcessor {
		void process(String message);
	}

	// Sends messages
	private static class OutputWorker extends Thread {

		private DataOutputStream stream;
		private Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
		private boolean hasNewMessage = false;

		OutputWorker(DataOutputStream stream){
			this.stream = stream;
			this.setDaemon(true);
		}

		@Override
		public void run(){
			while(true){
				try {
					while(!queue.isEmpty()) {
						synchronized (stream) {
							// Send message
							stream.write(queue.poll());
						}
					}
				} catch (IOException e) {
					// Stop thread on (socket) error
					break;
				}

				// Sleep untill new message
				try {
					Thread.sleep(9000);
				} catch (InterruptedException e) {
					synchronized (stream) {
						if (!hasNewMessage) {
							break;
						}
						hasNewMessage = false;
					}
				}
			}
		}

		public void send(byte[] message){
			queue.add(message);

			// Stop sleep
			synchronized (stream) {
				hasNewMessage = true;
				interrupt();
			}
		}

		// Return number of unsent messages
		public int unsent(){
			return queue.size();
		}
	}

	// Retrieves messages
	private class InputWorker implements Runnable{

		private DataInputStream stream;

		InputWorker(DataInputStream stream){
			this.stream = stream;
		}

		@Override
		public void run(){
			StringBuilder message = new StringBuilder();
			while (!isInterrupted()){ // Read until interrupted
				try {
					byte b = stream.readByte(); // Read next byte
					if (b == TERMINATOR){ // When terminator byte is reached

						// Process message
						process(message.toString());

						// Clear message
						message = new StringBuilder();
					}else{
						// Append byte to message
						message.append((char)b);
					}
				}catch (IOException e){
					break;
				}
			}
		}

		// Traverse data processors
		private void process(String message) {
			for(DataProcessor processor: dataProcessors) {
				processor.process(message);
			}
		}
	}

	public TCPConnection(int port) {
		PORT = port;
		this.setDaemon(true); // Make sure thread closes when application does.
	}

	@Override
	public void run() {
		//If we get a connection related exception -> try connecting again
		while (!isInterrupted()) { // Run until interrupted
			try {
				// Open socket and accept connection
				ServerSocket ss = new ServerSocket(PORT);
				socket = ss.accept();

				System.out.println("TCP connection established");

				outputWorker = new OutputWorker(new DataOutputStream(socket.getOutputStream()));
				inputWorker = new InputWorker(new DataInputStream(socket.getInputStream()));

				outputWorker.start();
				inputWorker.run(); // Blocking call


			} catch (IOException e) {
				// Connection dropped (stuff in here will be spammed)
			} finally {

				//Close socket since it wont have been if an exception was thrown
				closeSocket();

				socket = null;
				inputWorker = null;
				outputWorker = null;
			}
		}


		outputWorker.interrupt();
		try {
			outputWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void closeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}

	@Override
	public void interrupt() {
		closeSocket();
		super.interrupt();
	}

	/**
	 * Send a message over the TCP connection
	 * @param message The raw message to be sent
	 */
	public void send(String message) {
		try {
			outputWorker.send(message.concat(Character.toString(TERMINATOR)).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported character set: \"UTF-8\"");
			e.printStackTrace();
		}
	}

	/**
	 * Add a {@link DataProcessor} to be called when a network message is received.
	 * @param processor
	 * @return Was the operation successful?
	 */
	public boolean addDataProcessor(DataProcessor processor) {
		return dataProcessors.add(processor);
	}

	/**
	 * Remove a {@link DataProcessor}.
	 * @param processor
	 */
	public void removeDataProcessor(DataProcessor processor) {
		dataProcessors.remove(processor);
	}
}
