package yuudaari.soulus.common.block.fossil.fossil_netherrack;

import net.minecraft.util.BlockRenderLayer;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

import javax.annotation.Nonnull;

public class FossilNetherrack extends ModBlock {

	public FossilNetherrack () {
		this("fossil_netherrack");
	}

	public FossilNetherrack (String name) {
		super(name, new Material(MapColor.NETHERRACK));
		setHasItem();
		setHardness(0.4F);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.STONE);
	}

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
