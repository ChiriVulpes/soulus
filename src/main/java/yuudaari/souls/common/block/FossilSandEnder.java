package yuudaari.souls.common.block;

import yuudaari.souls.common.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collections;
import java.util.List;

public class FossilSandEnder extends FossilSand {
	public FossilSandEnder() {
		super("fossil_sand_ender");
		setHasItem();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModItems.BONE_CHUNK_ENDER.getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}
