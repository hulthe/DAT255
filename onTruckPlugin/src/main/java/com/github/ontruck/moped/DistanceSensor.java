package com.github.ontruck.moped;

import com.github.ontruck.util.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.ontruck.util.MathUtils.getDiffFromAverage;
import static com.github.ontruck.util.MathUtils.removeMostDifferentValues;
import static com.github.ontruck.util.MathUtils.weightedAverage;

public class DistanceSensor implements IDistanceSensor {

	/**
	 * The maximum size of the buffer.
	 */
	private static final int maxBufferLength = 64;

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

	public Tuple<Long, Integer> getLatestFilteredDistance() throws IndexOutOfBoundsException {
		return getFilteredDistance(0);
	}

	public Tuple<Long, Integer> getFilteredDistance(int offset) throws IndexOutOfBoundsException {
		if(buffer.size() < 3) {
			return new Tuple<>((long)0, 0);
		}

		Tuple<Long, Integer>[] filtered = removeMostDifferentValues(1,
				getRawDistance(offset),
				getRawDistance(Math.abs(offset) + 1),
				getRawDistance(Math.abs(offset) + 2)
		);

		int average = weightedAverage(filtered, new int[] {2, 1});

		return new Tuple<>(getRawDistance(offset).getX(), average);
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
