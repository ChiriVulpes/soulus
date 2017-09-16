package yuudaari.souls.common.block;

import yuudaari.souls.common.ModItems;
import yuudaari.souls.common.util.Material;
import yuudaari.souls.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

public class FossilDirt extends ModBlock {
	public FossilDirt() {
		super("fossil_dirt", new Material(MapColor.DIRT).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setResistance(2.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModItems.BONE_CHUNK_NORMAL.getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}
