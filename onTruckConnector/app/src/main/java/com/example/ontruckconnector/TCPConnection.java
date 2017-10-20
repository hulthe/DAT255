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

/**
 * Handles the TCP connection.
 */
public class TCPConnection extends AsyncTask<String, Void, TCPConnection> {

	/**
	 * The terminator character. It represents the end of a message.
	 */
	private static final char TERMINATOR = 0x04;

	/**
	 * The address the the communication is sent to.
	 */
	private final String IP_ADDRESS;

	/**
	 * The port that the communication is sent to.
	 */
	private final int PORT;

	/**
	 * The time between sent packets.
	 */
	private final int TIMEOUT = 1000;

	/**
	 * Whether there is a connection or not.
	 */
	private boolean isConnected = false;

	/**
	 * The object that sends data.
	 */
	private OutputWorker outputWorker = null;

	/**
	 * The object that receives data.
	 */
	private InputWorker inputWorker = null;

	/**
	 * The socket data is sent through.
	 */
	private Socket socket = null;


	/**
	 * Handles the sending of data as a thread.
	 */
	private class OutputWorker extends Thread {

		/**
		 * The stream where to data is placed in.
		 */
		private DataOutputStream stream;

		/**
		 * The queue with messages to be sent.
		 */
		private Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

		/**
		 * Whether the thread is running or not.
		 */
		private boolean running = false;

		/**
		 * Creates an {@link OutputWorker} with a given {@link DataOutputStream}.
		 *
		 * @param stream the given DataOutputStream.
		 */
		OutputWorker(DataOutputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			running = true;
			while (running) {
				try {
					while (!queue.isEmpty()) {
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

		/**
		 * Sends a message by adding a given message in the queue and then waking the thread.
		 *
		 * @param message the given message.
		 */
		public void send(byte[] message) {
			queue.add(message);
			// TODO: Find out if this crashes the app while blocking the gui thread.
			synchronized (stream) {
				interrupt();
			}
		}

		/**
		 * Receives the number of unsent messages in the queue.
		 *
		 * @return the number of unsent messages in the queue.
		 */
		public int unsent() {
			return queue.size();
		}
	}

	/**
	 * This is an inner class used to handle all inputs.
	 */
	private class InputWorker implements Runnable {

		/**
		 * The {@link DataInputStream} that holds the input.
		 */
		private DataInputStream stream;

		/**
		 * The list of {@link DataProcessor}s to handle te input.
		 */
		private List<DataProcessor> dataProcessors = new LinkedList<>();


		/**
		 * Creates an {@link InputWorker} with a given {@link DataInputStream}.
		 *
		 * @param stream the given {@link DataInputStream}.
		 */
		InputWorker(DataInputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			StringBuilder message = new StringBuilder();
			while (true) {
				try {
					byte b = stream.readByte();
					if (b == TERMINATOR) {
						process(message.toString());
						message = new StringBuilder();
					} else {
						message.append(b);
					}
				} catch (IOException e) {
					break;
				}

			}
		}

		/**
		 * Processes the given message with all the {@link DataProcessor}s in
		 * {@link TCPConnection.InputWorker#dataProcessors}.
		 *
		 * @param message the given message to be processed.
		 */
		private void process(String message) {
			for (DataProcessor processor : dataProcessors) {
				processor.process(message);
			}
		}
	}

	/**
	 * Internal interface to make sure that all {@link DataProcessor}s have a
	 * {@link DataProcessor#process(String)} method.
	 */
	public interface DataProcessor {
		void process(String message);
	}

	/**
	 * Gets called when this task starts and currently its only purpose is to run().
	 *
	 * @param strings
	 * @return
	 */
	@Override
	protected TCPConnection doInBackground(String... strings) {
		run();
		return null;
	}

	/**
	 * Creates a {@link TCPConnection} with a given
	 *
	 * @param ipAddress
	 * @param port
	 */
	public TCPConnection(String ipAddress, int port) {
		Log.i("TCP", "inside TCPConnection constructor");
		IP_ADDRESS = ipAddress;
		PORT = port;
	}


	/**
	 * The run method runs infinitely as long as the this task hasn't been canceled.
	 * The run method is suppose to attempt a TCP connection to the given IP-address.
	 */
	public void run() {

		Log.i("TCP", "inside TCPConnection.run()");

		//If we get a connection related exception -> try connecting again
		while (!isCancelled()) {
			try {
				// Open socket
				socket = new Socket();
				Log.i("TCP", "creating new socket with IP: " + IP_ADDRESS.toString());
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

	/**
	 * This method sends a message to the outputWorker's send() command.
	 *
	 * @param message
	 */
	public void send(String message) {
		if (outputWorker != null) {
			outputWorker.send(message.concat(Character.toString(TERMINATOR)).getBytes());
		}
	}

	/**
	 * This method adds the given DataProcessor to the list of DataProcessors.
	 *
	 * @param processor
	 */
	public void addDataProcessor(DataProcessor processor) {
		inputWorker.dataProcessors.add(processor);
	}

	/**
	 * This method removes the given DataProcessor from the list of DataProcessors in this class
	 *
	 * @param processor
	 */
	public void removeDataProcessor(DataProcessor processor) {
		inputWorker.dataProcessors.remove(processor);
	}

	/**
	 * This method manually terminates the entire task.
	 */
	public final void stop() {
		cancel(true);
		onCancelled();
	}

	/**
	 * This method gets called automatically when this task is terminated.
	 */
	@Override
	protected void onCancelled() {
		Log.i("TCP", "inside onCancelled()");
		isConnected = false;
		onProgressUpdate();
		try {
			if (outputWorker != null) {
				outputWorker.end();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method gets called to potentially update the connection text between "connected" and "disconnected".
	 *
	 * @param values
	 */
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		GUIHolder.getInstance().setConnection(isConnected);
	}
}
