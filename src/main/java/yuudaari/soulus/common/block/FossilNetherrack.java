package yuudaari.soulus.common.block;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilNetherrack extends ModBlock {
	public FossilNetherrack() {
		this("fossil_netherrack");
	}

	public FossilNetherrack(String name) {
		super(name, new Material(MapColor.NETHERRACK));
		setHasItem();
		setHardness(0.4F);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.STONE);
	}
}