package com.kallgirl.souls.common.block.Summoner;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
	}

	@Nonnull
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
		nbt.removeTag("SpawnPotentials");
		return nbt;
	}

	@Override
	public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}