package yuudaari.soulus.common.util;

public class RegionI {

	public Vec2i pos;
	public Vec2i size;

	public RegionI (Vec2i pos, Vec2i size) {
		this.pos = pos;
		this.size = size;
	}

	public RegionI (int x, int y, int w, int h) {
		this.pos = new Vec2i(x, y);
		this.size = new Vec2i(w, h);
	}

	public int x () {
		return pos.x;
	}

	public int y () {
		return pos.y;
	}

	public int w () {
		return size.x;
	}

	public int h () {
		return size.y;
	}

	public boolean isPosWithin (Vec2i pos) {
		return isPosWithin(pos.x, pos.y);
	}

	public boolean isPosWithin (int x, int y) {
		return x >= this.pos.x && y >= this.pos.y && //
			x < this.pos.x + this.size.x && y < this.pos.y + this.size.y;
	}
}
