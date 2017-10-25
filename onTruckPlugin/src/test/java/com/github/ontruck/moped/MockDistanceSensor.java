package com.github.ontruck.moped;

import com.github.ontruck.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDistanceSensor implements IDistanceSensor {

	List<Tuple<Long, Integer>> data;

	public MockDistanceSensor(Tuple<Long, Integer>... data) {
		this.data = new ArrayList<>(Arrays.asList(data));
	}

	public void addData(Tuple<Long, Integer> data) {
		this.data.addAll(Arrays.asList(data));
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public Tuple<Long, Integer> getLatestFilteredDistance() throws IndexOutOfBoundsException {
		return getLatestRawDistance();
	}

	@Override
	public Tuple<Long, Integer> getFilteredDistance(int offset) throws IndexOutOfBoundsException {
		return getRawDistance(offset);
	}

	@Override
	public Tuple<Long, Integer>[] getFilteredDistance() {
		return getRawDistance();
	}

	@Override
	public Tuple<Long, Integer> getLatestRawDistance() {
		return getRawDistance(0);
	}

	@Override
	public Tuple<Long, Integer> getRawDistance(int offset) throws IndexOutOfBoundsException {
		return data.get(data.size() - 1 - offset);
	}

	@Override
	public Tuple<Long, Integer>[] getRawDistance() {
		return data.toArray(new Tuple[0]);
	}
}
