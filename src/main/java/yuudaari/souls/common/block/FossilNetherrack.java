package yuudaari.souls.common.block;

import yuudaari.souls.common.util.Material;
import yuudaari.souls.common.ModObjects;
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

public class FossilNetherrack extends SoulsBlock {
	public FossilNetherrack() {
		super("fossil_netherrack", new Material(MapColor.NETHERRACK));
		setHasItem();
		setHardness(0.4F);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.STONE);
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModObjects.get("bone_chunk_nether").getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}