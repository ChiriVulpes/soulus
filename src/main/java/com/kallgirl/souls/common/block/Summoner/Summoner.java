package com.kallgirl.souls.common.block.Summoner;

import com.kallgirl.souls.ModInfo;
import com.kallgirl.souls.client.ResourceMap;
import com.kallgirl.souls.common.IModItem;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.SpawnMap;
import com.kallgirl.souls.common.item.Soulbook;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Summoner extends BlockMobSpawner implements IModItem {

	public Summoner() {
		super();
		setHardness(5.0F);
		setSoundType(SoundType.METAL);
		disableStats();
		String name = "summoner";
		setUnlocalizedName(name);
		GameRegistry.register(this, new ResourceLocation(ModInfo.MODID, name));
		GameRegistry.registerTileEntity(TileEntitySummoner.class, "Souls:Summoner");
	}

	@Nonnull
	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName()
			       .replaceAll("tile\\.", "tile." + ResourceMap.PREFIX_MOD);
	}

	public static class TileEntitySummoner extends TileEntityMobSpawner {
		private final SummonerLogic summonerLogic = new SummonerLogic() {
			@Nonnull
			@Override
			public World getSpawnerWorld () {
				return worldObj;
			}

			@Nonnull
			@Override
			public BlockPos getSpawnerPosition () {
				return pos;
			}
		};

		@Nonnull
		@Override
		public MobSpawnerBaseLogic getSpawnerBaseLogic () {
			return summonerLogic;
		}

		@Override
		public boolean receiveClientEvent(int id, int type) {
			return summonerLogic.setDelayToMin(id);
		}

		@Override
		public void update()
		{
			summonerLogic.updateSpawner();
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound.copy());
			summonerLogic.readFromNBT(compound);
		}

		@Nonnull
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			summonerLogic.writeToNBT(compound);
			return compound;
		}
	}

	@Override
	public int getExpDrop (IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return 0;
	}

	private static String getSpawnerMobTarget(TileEntityMobSpawner mobSpawnerTileEntity) {
		NBTTagCompound mobData = mobSpawnerTileEntity.getSpawnerBaseLogic().writeToNBT(new NBTTagCompound());
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

		TileEntitySummoner tileEntity = (TileEntitySummoner) world.getTileEntity(pos);
		String mobTarget = Summoner.getSpawnerMobTarget(tileEntity);
		ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
		Soulbook.setMobTarget(soulbook, mobTarget);
		Soulbook.setContainedEssence(soulbook, SpawnMap.map.get(mobTarget).required);
		drops.add(soulbook);

		return drops;
	}

	@Override
	@Nonnull
	public TileEntity createNewTileEntity (World worldIn, int meta) {
		return new TileEntitySummoner();
	}

	@Override
	public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntitySummoner mobSpawner = (TileEntitySummoner)world.getTileEntity(pos);
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
