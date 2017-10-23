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

		// Traverse dataprocessors
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
				System.out.println("breaking tcp");


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

		System.out.println("outputWorker");

		outputWorker.interrupt();
		System.out.println("outputWorker2");
		try {
			outputWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("outputWorker3");
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

	public void send(String message) {
		try {
			outputWorker.send(message.concat(Character.toString(TERMINATOR)).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported character set: \"UTF-8\"");
			e.printStackTrace();
		}
	}

	public void addDataProcessor(DataProcessor processor) {
		dataProcessors.add(processor);
	}

	public void removeDataProcessor(DataProcessor processor) {
		dataProcessors.remove(processor);
	}

}
