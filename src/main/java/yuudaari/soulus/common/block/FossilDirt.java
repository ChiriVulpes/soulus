package yuudaari.soulus.common.block;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilDirt extends ModBlock {

	public FossilDirt () {
		this("fossil_dirt");
	}

	public FossilDirt (String name) {
		super(name, new Material(MapColor.DIRT).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setResistance(2.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}
}
