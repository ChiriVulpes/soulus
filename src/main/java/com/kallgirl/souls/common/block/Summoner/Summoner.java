package com.kallgirl.souls.common.block.Summoner;

import com.kallgirl.souls.ModInfo;
import com.kallgirl.souls.client.ResourceMap;
import com.kallgirl.souls.common.IModItem;
import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.SpawnMap;
import com.kallgirl.souls.common.item.Soulbook;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class Summoner extends BlockContainer implements IModItem {

	public Summoner() {
		super(new Material(MapColor.STONE).setTransparent());
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		String name = "summoner";
		setUnlocalizedName(name);
		GameRegistry.register(this, new ResourceLocation(ModInfo.MODID, name));
		GameRegistry.registerTileEntity(SummonerTileEntity.class, "Souls:Summoner");
		isBlockContainer = true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube (IBlockState state) {
		return false;
	}

	@Nonnull
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly (Side.CLIENT)
	@Nonnull
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Nonnull
	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().replaceAll("tile\\.", "tile." + ResourceMap.PREFIX_MOD);
	}

	@Override
	public int getExpDrop (IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return 0;
	}

	private static String getSpawnerMobTarget(SummonerTileEntity mobSpawnerTileEntity) {
		NBTTagCompound mobData = mobSpawnerTileEntity.getLogic().writeToNBT(new NBTTagCompound());
		if (!mobData.hasKey("SpawnData", 10)) throw new RuntimeException("Spawner has no SpawnData");
		NBTTagCompound spawnData = mobData.getCompoundTag("SpawnData");
		if (!spawnData.hasKey("id", 8)) throw new RuntimeException("Spawner SpawnData has no mob id");
		return spawnData.getString("id");
	}

	@Nonnull
	@Override
	public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<>();

		drops.add(ModObjects.getBlock("emptySummoner").getItemStack());

		SummonerTileEntity tileEntity = (SummonerTileEntity) world.getTileEntity(pos);
		String mobTarget = Summoner.getSpawnerMobTarget(tileEntity);
		ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
		Soulbook.setMobTarget(soulbook, mobTarget);
		Soulbook.setContainedEssence(soulbook, SpawnMap.map.get(mobTarget).required);
		drops.add(soulbook);

		return drops;
	}

	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public TileEntity createNewTileEntity (World worldIn, int meta) {
		return new SummonerTileEntity();
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
