package com.tmcprojekt.tiles;

public class PointD {
	public double x, y;

	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public PointD() {
		this(0, 0);
	}

	@Override
	public String toString() {
		return "(" + Double.toString(x) + "," + Double.toString(y) + ")";
	}
}