package com.example.ontruckconnector;

import android.os.AsyncTask;
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


public class TCPConnection extends AsyncTask<String, Void, TCPConnection> {

	private static final char TERMINATOR = 0x04;

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
							Log.i("OutputWorker", "deepest inside run()");
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
			Log.i("TCP", "inside end()");
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

	public TCPConnection(String ipAddress, int port) {
		Log.i("TCP", "inside TCPConnection constructor");
		IP_ADDRESS = ipAddress;
		PORT = port;
	}



	public void run() {

		Log.i("TCP", "inside TCPConnection.run()");

		//If we get a connection related exception -> try connecting again
		while (!isCancelled()) {
			try {
				// Open socket
				socket = new Socket();
				Log.i("TCP", "creating new socket with IP: "+IP_ADDRESS.toString());
				socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), TIMEOUT);

				isConnected = true;
				this.publishProgress();

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
						Log.i("TCP", "closing socket inside finally{}");
						socket.close();
						socket = null;

						//Exception handling
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			isConnected = false;
			this.publishProgress();
			inputWorker = null;
			outputWorker = null;
			Log.i("TCP", "reached end of run() inside TCPConnection");
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

	//manually close this entire class/task
	public final void stop() {
		cancel(true);
		onCancelled();
	}

	//what happens after this class/task has been terminated
	@Override
	protected void onCancelled(){
		Log.i("TCP", "inside onCancelled()");
		isConnected = false;
		onProgressUpdate();
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
		GUIHolder.getInstance().setConnection(isConnected);
	}
}
