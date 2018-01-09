package yuudaari.soulus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MobPoof implements IMessage {
	public MobPoof() {
	}

	private BlockPos pos;

	public BlockPos getPos() {
		return pos;
	}

	public MobPoof(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

}