package com.github.ontruck;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DistanceSensorFilter implements DataCollector.DataProcessor {

	/**
	 * The maximum size of the buffer.
	 */
	private final int maxBufferLength = 5;

	/**
	 * The buffer that holds the latest sensor data.
	 */
	private Queue<short[]> buffer;


	public DistanceSensorFilter() {
		this.buffer = new LinkedList<>();
	}

	/**
	 * Adds given data to {@link DistanceSensorFilter#buffer} and removes first added
	 * element (FIFO).
	 *
	 * @param data the given sensor data.
	 */
	private void addToQueue(short[] data) {
		synchronized (buffer) {
			buffer.add(data);
			if (buffer.size() > maxBufferLength) {
				buffer.remove();
			}
		}
	}

	@Override
	public void process(short[] data) {
		System.out.println("process(" + Arrays.toString(data) + ")");
		addToQueue(data);
	}

	/**
	 * Filters a set of data into a distance that is then returned to the caller.
	 *
	 * @param data the data to be filtered.
	 * @return the distance recorded by the sensors and then extracted from the input
	 * data.
	 */
	private short filter(short[] data) {
		System.out.println("filter(" + Arrays.toString(data) + ")");
		System.out.println("getMinValue returns: " + Short.toString(getMinValue(data))
				+ ")");
		return getMinValue(data);
	}

	/**
	 * Returns the minimal value from the given array.
	 *
	 * @param array the given array.
	 * @return the minimal value.
	 */
	private short getMinValue(short[] array) {
		short tmp = array[0];
		if (array.length < 2) {
			return tmp;
		}
		for (int i = 1; i < array.length; i++) {
			if (array[i] < tmp) {
				tmp = array[i];
			}
		}
		return tmp;
	}

	/**
	 * @return the distance recorded by the sensors.
	 */
	public short getDistance() {
		return filter(buffer.peek());
	}
}
