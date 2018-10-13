package yuudaari.soulus.common.block.fossil.fossil_end;

import net.minecraft.util.BlockRenderLayer;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

import javax.annotation.Nonnull;

public class FossilEndStone extends ModBlock {

	public FossilEndStone () {
		this("fossil_end_stone");
	}

	public FossilEndStone (String name) {
		super(name, new Material(MapColor.SAND));
		setHasItem();
		setHardness(3.0F);
		setResistance(15.0F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.STONE);
	}

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
