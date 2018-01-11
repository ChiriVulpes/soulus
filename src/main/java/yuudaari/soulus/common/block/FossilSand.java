package yuudaari.soulus.common.block;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilSand extends ModBlock {

	public FossilSand () {
		this("fossil_sand");
	}

	public FossilSand (String name) {
		super(name, new Material(MapColor.SAND).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.SAND);
	}
}
