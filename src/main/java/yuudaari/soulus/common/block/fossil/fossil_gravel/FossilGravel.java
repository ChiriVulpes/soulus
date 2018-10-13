package yuudaari.soulus.common.block.fossil.fossil_gravel;

import net.minecraft.util.BlockRenderLayer;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

import javax.annotation.Nonnull;

public class FossilGravel extends ModBlock {

	public FossilGravel () {
		this("fossil_gravel");
	}

	public FossilGravel (String name) {
		super(name, new Material(MapColor.STONE).setToolNotRequired());
		setHasItem();
		setHardness(0.6F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
