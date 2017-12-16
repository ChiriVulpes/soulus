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

public class FossilDirt extends ModBlock {
	public FossilDirt() {
		this("fossil_dirt");
	}

	public FossilDirt(String name) {
		super(name, new Material(MapColor.DIRT).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setResistance(2.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModItems.BONE_CHUNK_NORMAL.getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}
