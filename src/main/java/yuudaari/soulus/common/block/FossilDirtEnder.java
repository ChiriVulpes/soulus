package yuudaari.soulus.common.block;

import yuudaari.soulus.common.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collections;
import java.util.List;

public class FossilDirtEnder extends FossilDirt {
	public FossilDirtEnder() {
		super("fossil_dirt_ender");
		setHasItem();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModItems.BONE_CHUNK_ENDER.getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}
