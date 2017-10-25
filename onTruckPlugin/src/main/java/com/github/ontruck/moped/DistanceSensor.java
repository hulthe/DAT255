package com.github.ontruck.moped;

import com.github.ontruck.util.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.ontruck.util.MathUtils.getDiffFromAverage;
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

	private Tuple<Long, Integer>[] removeMostDifferentValues(int count, Tuple<Long, Integer>... values) {
		int mostDifferent = 0;
		for (int i = 1; i < values.length; i++) {
			if (getDiffFromAverage(values[i], values) > getDiffFromAverage(values[mostDifferent], values)) {
				mostDifferent = i;
			}
		}

		Tuple<Long, Integer>[] returning = new Tuple[values.length - 1];

		int i2 = 0;
		for (int i = 0; i < values.length; i++) {
			if (i != mostDifferent) {
				returning[i2] = values[i];
				i2++;
			}
		}

		if (count == 1) {
			return returning;
		} else {
			return removeMostDifferentValues(count - 1, returning);
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
