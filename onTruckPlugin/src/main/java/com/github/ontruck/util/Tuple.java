package com.github.ontruck.util;

public class Tuple<X, Y> {
	private final X x;
	private final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Tuple) {
			Tuple other = (Tuple)o;
			return x.equals(other.x) && y.equals(other.y);
		}
		return super.equals(o);
	}
}
