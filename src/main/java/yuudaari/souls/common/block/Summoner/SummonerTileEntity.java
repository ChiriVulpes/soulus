package yuudaari.souls.common.block.Summoner;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.souls.Souls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SummonerTileEntity extends TileEntity implements ITickable {
	private final SummonerLogic logic = new SummonerLogic() {
		@Nonnull
		@Override
		public World getSpawnerWorld() {
			return world;
		}

		@Nonnull
		@Override
		public BlockPos getSpawnerPosition() {
			return pos;
		}
	};

	@Nonnull
	public SummonerLogic getLogic() {
		return logic;
	}

	@Override
	public void update() {
		logic.update();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		logic.readFromNBT(compound);
	}

	public String getMob() {
		return logic.getMobName();
	}

	public void setMob(String mobName) {
		logic.setMobName(mobName);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		logic.writeToNBT(compound);
		return compound;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
		return nbt;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		Souls.LOGGER.info("onDataPacket");
		readFromNBT(pkt.getNbtCompound());
	}
}