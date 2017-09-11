package plugins;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class UDPConnection extends Thread {
	private final List<DataProcessor> dataProcessors = new LinkedList<DataProcessor>();

	private final byte[] message = new byte[6];

	private final DatagramSocket socket;
	private final DatagramPacket packet= new DatagramPacket(message, message.length);

	public interface DataProcessor{
		void process(byte[] data);
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
			}catch (IOException ex){
				System.err.printf("Error while reading from socket: %s \n", ex.getMessage());
			}
		}
		System.out.printf("UDP thread closed\n");
	}

	private void process(byte[] data){
		if (validate(data)){
			DataProcessor[] ProcessorList;

			// Synchronized copy to avoid concurrency problems
			synchronized (dataProcessors){
				ProcessorList = dataProcessors.toArray(new DataProcessor[dataProcessors.size()]);
			}

			for (DataProcessor processor : ProcessorList) {
				processor.process(data);
			}
		}
	}

	private boolean validate(byte[] data){
		return true; //Todo
	}

	@Override
	public void interrupt(){
		this.socket.close(); // Close the socket before interrupt
		super.interrupt();
	}
}