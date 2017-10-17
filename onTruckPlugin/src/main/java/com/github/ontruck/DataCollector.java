package com.github.ontruck;

import com.github.moped.jcan.CAN;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to collect data from the sensor on the CAN network.
 */
public class DataCollector extends Thread {

	/**
	 * Where the collected data is stored before it is processed.
	 */
	private short[] data;

	/**
	 * The {@link CAN} network
	 */
	private final CAN can;

	/**
	 * The list of {@link DataProcessor}s that will process the data sent from the
	 * {@link CAN} network.
	 */
	private final List<DataProcessor> dataProcessors = new LinkedList<>();


	/**
	 * Creates a {@link DataProcessor} connected to a given {@link CAN} network.
	 *
	 * @param can the given CAN network that the DataProcessor will be
	 *            connected to.
	 */
	public DataCollector(CAN can) {
		this.can = can;
	}

	/**
	 * Adds a {@link DataProcessor} to the {@link DataCollector#dataProcessors} list.
	 *
	 * Note: To be able to remove the added DataProcessor you will have to save the
	 * reference to the given DataProcessor.
	 *
	 * @param processor  the given DataProcessor that should be added to the list.
	 */
	public void addDataProcessor(DataProcessor processor) {
		synchronized (dataProcessors) {
			this.dataProcessors.add(processor);
		}
	}

	/**
	 * Removes the {@link DataProcessor} to the {@link DataCollector#dataProcessors} list.
	 *
	 * @param processor  the given DataProcessor that should be added to the list.
	 */
	public void removeDataProcessor(DataProcessor processor) {
		synchronized (dataProcessors) {
			this.dataProcessors.remove(processor);
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				data = can.readSensor();
				process(data);
			} catch (InterruptedException e) {
				this.interrupt();
			}

		}
	}

	/**
	 * Process the given data.
	 *
	 * @param data  the given data.
	 */
	private void process(short[] data) {
		DataProcessor[] ProcessorList;

		// Synchronized copy to avoid concurrency problems
		synchronized (dataProcessors) {
			ProcessorList = dataProcessors.toArray(new DataProcessor[dataProcessors.size()]);
		}

		for (DataProcessor processor : ProcessorList) {
			processor.process(data);
		}
	}

	/**
	 * An interface for processors placed in the {@link DataCollector#dataProcessors}.
	 */
	public interface DataProcessor {
		void process(short[] data);
	}
}
