package com.kallgirl.souls.common.block;

import com.kallgirl.souls.common.util.Material;
import com.kallgirl.souls.common.ModObjects;
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

public class FossilNetherrackEnder extends Block {
	public FossilNetherrackEnder () {
		super("fossilNetherrackEnder", new Material(MapColor.NETHERRACK));
		setHasItem();
		setHardness(0.4F);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.STONE);
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack boneChunk = ModObjects.get("boneChunkEnder").getItemStack(4);
		return Collections.singletonList(boneChunk);
	}
}