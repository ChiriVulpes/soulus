package com.kallgirl.souls.common.block.Summoner;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SummonerTileEntity extends TileEntity implements ITickable {
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


	public static void registerFixesMobSpawner(DataFixer fixer) {
		fixer.registerWalker(FixTypes.BLOCK_ENTITY, (IDataFixer innerFixer, NBTTagCompound compound, int versionIn) -> {
			if ("MobSpawner".equals(compound.getString("id"))) {
				if (compound.hasKey("SpawnPotentials", 9)) {
					NBTTagList nbttaglist = compound.getTagList("SpawnPotentials", 10);

					for (int i = 0; i < nbttaglist.tagCount(); ++i) {
						NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
						nbttagcompound.setTag("Entity", innerFixer.process(FixTypes.ENTITY, nbttagcompound.getCompoundTag("Entity"), versionIn));
					}
				}

				compound.setTag("SpawnData", innerFixer.process(FixTypes.ENTITY, compound.getCompoundTag("SpawnData"), versionIn));
			}

			return compound;
		});
	}

	@Nonnull
	public SummonerLogic getLogic () {
		return summonerLogic;
	}

	public boolean receiveClientEvent(int id, int type) {
		return summonerLogic.setDelayToMin(id);
	}

	public void update()
	{
		summonerLogic.updateSpawner();
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		summonerLogic.readFromNBT(compound);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		summonerLogic.writeToNBT(compound);
		return compound;
	}

	@Override
	public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}