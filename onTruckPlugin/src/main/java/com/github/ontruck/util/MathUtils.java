package com.github.ontruck.util;

import java.util.stream.IntStream;

public class MathUtils {

	public static int average(Tuple<Long, Integer>... values) {

		int[] intValues = new int[values.length];

		for (int i = 0; i < values.length; i++) {
			intValues[i] = values[i].getY();
		}

		return (int)Math.round(IntStream.of(intValues).average().getAsDouble());
	}

	// Difference between Y value of first tuple and average of the rest
	public static int getDiffFromAverage(Tuple<Long, Integer> val, Tuple<Long, Integer>... others) {
		return Math.abs(val.getY() - average(others));
	}

	public static int weightedAverage(Tuple<Long, Integer>[] values, int[] weights) {

		if(values.length != weights.length) {
			throw new IllegalArgumentException();
		}

		int[] weightedValues = new int[values.length];
		for(int i = 0; i < values.length; i++) {
			weightedValues[i] = values[i].getY() * weights[i];
		}

		return IntStream.of(weightedValues).sum() / IntStream.of(weights).sum();
	}

	public static Tuple<Long, Integer>[] removeMostDifferentValues(int count, Tuple<Long, Integer>... values) {
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
}
