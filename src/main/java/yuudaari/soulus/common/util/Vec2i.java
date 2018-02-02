package yuudaari.soulus.common.util;

public class Vec2i {

	public static final Vec2i ZERO = new Vec2i(0, 0);
	public static final Vec2i ONE = new Vec2i(1, 1);
	public static final Vec2i UNIT_X = new Vec2i(1, 0);
	public static final Vec2i NEGATIVE_UNIT_X = new Vec2i(-1, 0);
	public static final Vec2i UNIT_Y = new Vec2i(0, 1);
	public static final Vec2i NEGATIVE_UNIT_Y = new Vec2i(0, -1);
	public static final Vec2i MAX = new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
	public static final Vec2i MIN = new Vec2i(Integer.MIN_VALUE, Integer.MIN_VALUE);

	public final int x;
	public final int y;

	public Vec2i (int xIn, int yIn) {
		this.x = xIn;
		this.y = yIn;
	}
}
