package yuudaari.souls.common.util;

import net.minecraft.block.material.MapColor;

public class Material extends net.minecraft.block.material.Material {
	public Material(MapColor color) {
		super(color);
	}

	private Boolean toolRequired = true;

	public Material setToolNotRequired() {
		toolRequired = false;
		return this;
	}

	private Boolean isLiquid = false;

	public Material setLiquid() {
		isLiquid = true;
		return this;
	}

	private Boolean isSolid = true;

	public Material setNonsolid() {
		isSolid = false;
		return this;
	}

	private Boolean blocksLight = true;

	public Material setTransparent() {
		blocksLight = false;
		isOpaque = false;
		return this;
	}

	private Boolean isOpaque = true;

	public Material setTranslucent() {
		isOpaque = false;
		return this;
	}

	private Boolean blocksMovement = true;

	public Material setPermissible() {
		blocksMovement = false;
		return this;
	}

	public Material setFlammable() {
		return this;
	}

	public Material setImmovable() {
		setImmovableMobility();
		return this;
	}

	public Material setDestroyOnPushed() {
		setNoPushMobility();
		return this;
	}

	@Override
	public boolean isToolNotRequired() {
		return !toolRequired;
	}

	@Override
	public boolean isSolid() {
		return isSolid;
	}

	@Override
	public boolean isLiquid() {
		return isLiquid;
	}

	@Override
	public boolean blocksLight() {
		return blocksLight;
	}

	@Override
	public boolean blocksMovement() {
		return blocksMovement;
	}

	@Override
	public boolean isOpaque() {
		return isOpaque;
	}
}
