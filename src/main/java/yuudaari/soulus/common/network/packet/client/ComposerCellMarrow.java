package yuudaari.soulus.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;

public class ComposerCellMarrow implements IMessage {

	public ComposerCellMarrow () {
	}

	public int dimension;
	public BlockPos pos;
	public int chunkId;

	public ComposerCellMarrow (final ComposerCellTileEntity te, final Item chunk) {
		dimension = te.getWorld().provider.getDimension();
		pos = te.getPos();
		chunkId = Item.getIdFromItem(chunk);
	}

	@Override
	public void toBytes (ByteBuf buf) {
		buf.writeInt(dimension);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(chunkId);
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		dimension = buf.readInt();
		final int x = buf.readInt();
		final int y = buf.readInt();
		final int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		chunkId = buf.readInt();
	}

}
