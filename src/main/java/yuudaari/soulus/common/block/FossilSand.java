package yuudaari.soulus.common.block;

import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collections;
import java.util.List;

public class FossilSand extends ModBlock {
	public FossilSand() {
		this("fossil_sand");
	}

	public FossilSand(String name) {
		super(name, new Material(MapColor.SAND).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.SAND);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModItems.BONE_CHUNK_DRY.getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}
