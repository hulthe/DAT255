package com.github.ontruck;

import javafx.util.Pair;

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
	private final List<Pair<Long, Integer>> buffer;


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
			buffer.add(new Pair<>(System.currentTimeMillis(), data));
			while (buffer.size() > maxBufferLength) {
				buffer.remove(0);
			}
		}
	}

	public int getLatesteFilteredDistance() {
		return getLatestRawDistance(); // FIXME
	}

	public Pair<Long, Integer> getFilteredDistance(int offset) throws ArrayIndexOutOfBoundsException {
		return getRawDistance(offset); // FIXME
	}

	public Pair<Long, Integer>[] getFilteredDistance() {
		return getRawDistance(); // FIXME
	}

	/**
	 * @return the distance recorded by the sensors.
	 */
	public int getLatestRawDistance() {
		return buffer.get(buffer.size() - 1).getValue();
	}

	public Pair<Long, Integer> getRawDistance(int offset) throws ArrayIndexOutOfBoundsException {
		return buffer.get(buffer.size() - 1 - Math.abs(offset));
	}

	public Pair<Long, Integer>[] getRawDistance() {
		return buffer.toArray(new Pair[0]);
	}
}
