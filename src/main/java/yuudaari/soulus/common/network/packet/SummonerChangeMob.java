package yuudaari.soulus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.common.block.Summoner.SummonerTileEntity;

public class SummonerChangeMob implements IMessage {
	public SummonerChangeMob() {
	}

	public BlockPos pos;
	public String mob;

	public SummonerChangeMob(SummonerTileEntity te, String newMob) {
		pos = te.getPos();
		mob = newMob;
	}

	public SummonerTileEntity getTileEntity() {
		return (SummonerTileEntity) Minecraft.getMinecraft().world.getTileEntity(pos);
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

	public static class Handler implements IMessageHandler<SummonerChangeMob, IMessage> {
		@Override
		public IMessage onMessage(SummonerChangeMob message, MessageContext ctx) {
			SummonerTileEntity summoner = message.getTileEntity();
			summoner.setMob(message.mob);
			summoner.reset();
			return null;
		}
	}
}