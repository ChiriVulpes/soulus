package yuudaari.soulus.common.block;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilGravel extends ModBlock {
	public FossilGravel() {
		this("fossil_gravel_scale");
	}

	public FossilGravel(String name) {
		super(name, new Material(MapColor.STONE).setToolNotRequired());
		setHasItem();
		setHardness(0.6F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}
}
