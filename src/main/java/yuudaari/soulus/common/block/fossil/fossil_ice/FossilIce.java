package yuudaari.soulus.common.block.fossil.fossil_ice;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.BlockRenderLayer;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;

import javax.annotation.Nonnull;

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
		setSoundType(SoundType.GLASS);
	}

	@Override
	public boolean isOpaqueCube (net.minecraft.block.state.IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube (net.minecraft.block.state.IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube (net.minecraft.block.state.IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
