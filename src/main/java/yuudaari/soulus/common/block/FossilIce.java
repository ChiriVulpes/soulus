package yuudaari.soulus.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;

public class FossilIce extends ModBlock {

	public FossilIce() {
		this("fossil_ice");
	}

	public FossilIce(String name) {
		super(name, new Material(MapColor.ICE).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setResistance(2.5F);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.GROUND);
	}
}
