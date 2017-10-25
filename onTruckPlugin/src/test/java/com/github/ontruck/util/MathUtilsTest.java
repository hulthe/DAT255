package com.github.ontruck.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathUtilsTest {

	@Test
	public void average() {
		assertEquals(
				MathUtils.average(
						new Tuple<>((long) 0, 1),
						new Tuple<>((long) 0, 2),
						new Tuple<>((long) 0, 3)
				), 2
		);
	}

	// Difference between Y value of first tuple and average of the rest
	@Test
	public void getDiffFromAverage() {
		assertEquals(
				"",
				MathUtils.getDiffFromAverage(
						new Tuple<>((long) 0, 8),
						new Tuple<>((long) 0, 1),
						new Tuple<>((long) 0, 2),
						new Tuple<>((long) 0, 3)
				), 6

		);
	}

	@Test
	public void weightedAverage() {
		assertEquals(
				"Calculate weighted average",
				MathUtils.weightedAverage(
						new Tuple[] {
								new Tuple<>((long) 0, 9),
								new Tuple<>((long) 0, 3),
								new Tuple<>((long) 0, 2)
						},
						new int[] {1, 2, 8}
				), 2
		);
	}

	@Test
	public void removeMostDifferentValues(){
		Tuple<Long, Integer>[] input = new Tuple[] {
				new Tuple<>((long) 0, 1),
				new Tuple<>((long) 0, 2),
				new Tuple<>((long) 0, -69),
				new Tuple<>((long) 0, 3),
				new Tuple<>((long) 0, 4),
				new Tuple<>((long) 0, 5),
				new Tuple<>((long) 0, 6),
				new Tuple<>((long) 0, 7),
				new Tuple<>((long) 0, 42)
		};

		Tuple<Long, Integer>[] expectedResult = new Tuple[] {
				new Tuple<>((long) 0, 1),
				new Tuple<>((long) 0, 2),
				new Tuple<>((long) 0, 3),
				new Tuple<>((long) 0, 4),
				new Tuple<>((long) 0, 5),
				new Tuple<>((long) 0, 6),
				new Tuple<>((long) 0, 7),
		};

		assertEquals(
				"Most different values removed",
				MathUtils.removeMostDifferentValues(2, input),
				expectedResult
		);
	}
}
