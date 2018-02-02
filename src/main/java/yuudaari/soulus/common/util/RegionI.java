package yuudaari.soulus.common.util;

public class RegionI {

	public Vec2i pos;
	public Vec2i size;

	public RegionI (Vec2i pos, Vec2i size) {
		this.pos = pos;
		this.size = size;
	}

	public boolean isPosWithin (Vec2i pos) {
		return pos.x >= this.pos.x && pos.y >= this.pos.y && //
			pos.x < this.pos.x + this.size.x && pos.y < this.pos.y + this.size.y;
	}
}
