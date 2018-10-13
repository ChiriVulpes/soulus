package yuudaari.soulus.common.block.fossil.fossil_sand;

import net.minecraft.util.BlockRenderLayer;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

import javax.annotation.Nonnull;

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

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
