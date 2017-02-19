package com.kallgirl.souls.common.block.Summoner;

import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.SpawnMap;
import com.kallgirl.souls.common.block.Block;
import com.kallgirl.souls.common.item.Soulbook;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EmptySummoner extends Block {
	public EmptySummoner () {
		super("emptySummoner", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		addRecipeShaped(
			new String[]{
				"BBB",
				"BEB",
				"BBB"
			},
			'B', ModObjects.get("endersteelBars"),
			'E', ModObjects.getItem("enderDust"),
			'S', ModObjects.get("soulbook")
		);
	}

	@Override
	public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (heldItem == null || heldItem.getItem() != ModObjects.getItem("soulbook")) return false;
		String mobTarget = Soulbook.getMobTarget(heldItem);
		int containedEssence = Soulbook.getContainedEssence(heldItem);
		if (mobTarget == null || containedEssence < SpawnMap.map.get(mobTarget).required) return false;
		IBlockState mobSpawner = ((Summoner) ModObjects.get("summoner")).getDefaultState();
		worldIn.setBlockState(pos, mobSpawner);
		SummonerTileEntity tileEntity = (SummonerTileEntity) worldIn.getTileEntity(pos);
		if (tileEntity == null)
			throw new RuntimeException("Mob spawner tile entity was not created. Something went wrong.");
		SummonerLogic logic = (SummonerLogic)tileEntity.getLogic();
		logic.setEntityName(mobTarget);
		playerIn.inventory.removeStackFromSlot(playerIn.inventory.currentItem);
		return true;
	}
}