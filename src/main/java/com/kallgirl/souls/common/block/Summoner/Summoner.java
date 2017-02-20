package com.kallgirl.souls.common.block.Summoner;

import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.SpawnMap;
import com.kallgirl.souls.common.block.Block;
import com.kallgirl.souls.common.item.Soulbook;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Summoner extends Block {

	private static String getSpawnerMobTarget(SummonerTileEntity summonerTileEntity) {
		NBTTagCompound mobData = summonerTileEntity.getLogic().writeToNBT(new NBTTagCompound());
		if (!mobData.hasKey("SpawnData", 10)) throw new RuntimeException("Spawner has no SpawnData");
		NBTTagCompound spawnData = mobData.getCompoundTag("SpawnData");
		if (!spawnData.hasKey("id", 8)) throw new RuntimeException("Spawner SpawnData has no mob id");
		return spawnData.getString("id");
	}

	public Summoner() {
		super("summoner", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		GameRegistry.registerTileEntity(SummonerTileEntity.class, "Souls:Summoner");
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return true;
	}

	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public TileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new SummonerTileEntity();
	}

	public static SummonerTileEntity lastBrokenSummoner = null;

	@SubscribeEvent
	public static void onSummonerBreak(BlockEvent.BreakEvent event) {
		if (event.getState().getBlock() == ModObjects.getBlock("summoner")) {
			lastBrokenSummoner = (SummonerTileEntity) event.getWorld().getTileEntity(event.getPos());
		}
	}

	@Nonnull
	@Override
	public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<>();

		drops.add(ModObjects.getBlock("emptySummoner").getItemStack());

		String mobTarget = Summoner.getSpawnerMobTarget(lastBrokenSummoner);
		lastBrokenSummoner = null; // gc last broken summoner tile entity as it's unused now
		ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
		Soulbook.setMobTarget(soulbook, mobTarget);
		Soulbook.setContainedEssence(soulbook, SpawnMap.map.get(mobTarget).required);
		drops.add(soulbook);

		return drops;
	}

	@Override
	public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			SummonerTileEntity mobSpawner = (SummonerTileEntity)world.getTileEntity(pos);
			String mobTarget = Summoner.getSpawnerMobTarget(mobSpawner);
			ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
			Soulbook.setMobTarget(soulbook, mobTarget);
			Soulbook.setContainedEssence(soulbook, SpawnMap.map.get(mobTarget).required);
			EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, soulbook);
			dropItem.setNoPickupDelay();
			world.spawnEntityInWorld(dropItem);
		}
		world.setBlockState(pos, ModObjects.getBlock("emptySummoner").getDefaultState());
		return true;
	}
}
