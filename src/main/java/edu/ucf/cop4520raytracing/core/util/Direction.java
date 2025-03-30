package edu.ucf.cop4520raytracing.core.util;

public enum Direction {
	FORWARD(0, 0, 1),
	BACKWARD(0, 0, -1),
	LEFT(1, 0, 0),
	RIGHT(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0);
	
	public final int x;
	public final int y;
	public final int z;
	
	/**
	 * Offsets
	 */
	Direction(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
