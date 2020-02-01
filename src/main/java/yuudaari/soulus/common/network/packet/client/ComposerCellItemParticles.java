package yuudaari.soulus.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ComposerCellItemParticles implements IMessage {

	public ComposerCellItemParticles () {
	}

	// public int dimension;
	public BlockPos pos;
	public int chunkId;
	public int count;

	public ComposerCellItemParticles (/*final World world,*/ final BlockPos pos, final int chunkId, final int count) {
		// dimension = world.provider.getDimension();
		this.pos = pos;
		this.chunkId = chunkId;
		this.count = count;
	}

	@Override
	public void toBytes (ByteBuf buf) {
		// buf.writeInt(dimension);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(chunkId);
		buf.writeInt(count);
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		// dimension = buf.readInt();
		final int x = buf.readInt();
		final int y = buf.readInt();
		final int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		chunkId = buf.readInt();
		count = buf.readInt();
	}

}
