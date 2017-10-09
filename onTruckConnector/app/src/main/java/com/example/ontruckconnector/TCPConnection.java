package com.example.ontruckconnector;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.currentThread;

public class TCPConnection extends AsyncTask<String, Void, TCPConnection> {

	private static final char TERMINATOR = '\n'; //FIXME: newline for now

	private final String IP_ADDRESS; 	//the IP_ADDRESS is set in the constructor
	private final int PORT;				//the port is set in the constructor
	private final int TIMEOUT = 1000;   //this is in ms and is the delay between pings
	private boolean isConnected = false;

	private OutputWorker outputWorker = null;
	private InputWorker inputWorker = null;
	private Socket socket = null;


	private class OutputWorker extends Thread {

		private DataOutputStream stream;
		private Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
		private boolean running = false;

		OutputWorker(DataOutputStream stream){
			this.stream = stream;
		}

		@Override
		public void run(){
			running = true;
			while(running){
				try {
					while(!queue.isEmpty()) {
						synchronized (stream) {
							stream.write(queue.poll());
						}
					}
				} catch (IOException e) {
					break;
				}

				try {
					Thread.sleep(9001);
				} catch (InterruptedException e) {
					// Carry on
				}
			}
		}

		public void end() {
			running = false;
			interrupt();
		}

		public void send(byte[] message){
			queue.add(message);
			// TODO: Find out if this crashes the app while blocking the gui thread.
			synchronized (stream) {
				interrupt();
			}
		}
		public int unsent(){
			return queue.size();
		}
	}
	private class InputWorker implements Runnable{

		private DataInputStream stream;
		private List<DataProcessor> dataProcessors = new LinkedList<>();

		InputWorker(DataInputStream stream){
			this.stream = stream;
		}

		@Override
		public void run(){
			StringBuilder message = new StringBuilder();
			while (true){
				try {
					byte b = stream.readByte();
					if (b == TERMINATOR){
						process(message.toString());
						message = new StringBuilder();
					}else{
						message.append(b);
					}
				}catch (IOException e){
					break;
				}

			}
		}

		private void process(String message) {
			for(DataProcessor processor: dataProcessors) {
				processor.process(message);
			}
		}
	}

	public interface DataProcessor {
		void process(String message);
	}

	@Override
	protected TCPConnection doInBackground(String... strings) {
		run();
		return null;
	}

	//public TCPConnection(String ipAddress, int port, MainActivity mainActivity) {
	public TCPConnection(String ipAddress, int port) {
		IP_ADDRESS = ipAddress;
		PORT = port;
	}



	public void run() {

		//If we get a connection related exception -> try connecting again
		while (!isCancelled()) {
			try {
				// Open socket
				socket = new Socket();
				Log.e("IP", IP_ADDRESS.toString());
				socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), TIMEOUT);

				isConnected = true;

				outputWorker = new OutputWorker(new DataOutputStream(socket.getOutputStream()));
				inputWorker = new InputWorker(new DataInputStream(socket.getInputStream()));

				addDataProcessor(new DataProcessor() {
					@Override
					public void process(String message) {
						System.out.println(message);
					}
				});

				outputWorker.start();
				inputWorker.run();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				//Close socket since it wont have been if an exception was thrown
				if (socket != null && !socket.isClosed()) {
					try {
						socket.close();
						socket = null;

						//Exception handling
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			isConnected = false;
			inputWorker = null;
			outputWorker = null;
		}
	}

	public void send(String message) {
		if(outputWorker != null){
			outputWorker.send(message.concat(Character.toString(TERMINATOR)).getBytes());
		}
	}

	public void addDataProcessor(DataProcessor processor) {
		inputWorker.dataProcessors.add(processor);
	}

	public void removeDataProcessor(DataProcessor processor) {
		inputWorker.dataProcessors.remove(processor);
	}

	@Override
	protected void onCancelled(){
		try {
			if(outputWorker != null) {
				outputWorker.end();
			}
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Gives the connection boolean to the Holder -> possibly toggle the connection text
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		ConnectionTextHolder.getInstance().setConnection(isConnected);
	}
}
