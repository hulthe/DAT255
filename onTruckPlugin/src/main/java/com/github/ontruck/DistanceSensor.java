package com.github.ontruck;

import java.util.LinkedList;
import java.util.List;

public class DistanceSensor {

	/**
	 * The maximum size of the buffer.
	 */
	private final int maxBufferLength = 64;

	/**
	 * The buffer that holds the latest sensor data.
	 */
	private final List<Tuple<Long, Integer>> buffer;


	public DistanceSensor() {
		this.buffer = new LinkedList<>();
	}

	/**
	 * Adds given data to {@link DistanceSensor#buffer} and removes first added
	 * element (FIFO).
	 *
	 * @param data the given sensor data.
	 */
	public void process(int data) {
		synchronized (buffer) {
			buffer.add(new Tuple<>(System.currentTimeMillis(), data));
			while (buffer.size() > maxBufferLength) {
				buffer.remove(0);
			}
		}
	}

	public int getBufferSize() {
		return buffer.size();
	}

	public Tuple<Long, Integer> getLatestFilteredDistance() {
		return getLatestRawDistance(); // FIXME
	}

	public Tuple<Long, Integer> getFilteredDistance(int offset) throws IndexOutOfBoundsException {
		return getRawDistance(offset); // FIXME
	}

	public Tuple<Long, Integer>[] getFilteredDistance() {
		return getRawDistance(); // FIXME
	}

	/**
	 * @return the distance recorded by the sensors.
	 */
	public Tuple<Long, Integer> getLatestRawDistance() {
		if(buffer.size() == 0) {
			return new Tuple<>((long)0, 0);
		}

		return buffer.get(buffer.size() - 1);
	}

	public Tuple<Long, Integer> getRawDistance(int offset) throws IndexOutOfBoundsException {
		return buffer.get(buffer.size() - 1 - Math.abs(offset));
	}

	public Tuple<Long, Integer>[] getRawDistance() {
		return buffer.toArray(new Tuple[0]);
	}
}
