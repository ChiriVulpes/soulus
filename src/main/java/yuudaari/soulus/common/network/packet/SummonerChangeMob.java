package yuudaari.soulus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yuudaari.soulus.common.block.summoner.SummonerTileEntity;
import yuudaari.soulus.common.util.Logger;

public class SummonerChangeMob implements IMessage {
	public SummonerChangeMob() {
	}

	public BlockPos pos;
	public String mob;

	public SummonerChangeMob(SummonerTileEntity te, String newMob) {
		Logger.info("change mob sent");
		pos = te.getPos();
		mob = newMob;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		ByteBufUtils.writeUTF8String(buf, mob);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		mob = ByteBufUtils.readUTF8String(buf);
	}

}