package com.github.ontruck.moped;

import com.github.ontruck.util.Tuple;

import java.util.stream.IntStream;

public interface IDistanceSensor {

	int getBufferSize();

	public void process(int data);

	Tuple<Long, Integer> getLatestFilteredDistance() throws IndexOutOfBoundsException;

	Tuple<Long, Integer> getFilteredDistance(int offset) throws IndexOutOfBoundsException;

	Tuple<Long, Integer>[] getFilteredDistance();

	Tuple<Long, Integer> getLatestRawDistance();

	Tuple<Long, Integer> getRawDistance(int offset) throws IndexOutOfBoundsException;

	Tuple<Long, Integer>[] getRawDistance();
}
