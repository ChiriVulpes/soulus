package yuudaari.soulus.common.network.packet.client;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;

public class ComposerCellMarrowHandler implements IMessageHandler<ComposerCellMarrow, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (ComposerCellMarrow message, MessageContext ctx) {
		ComposerCellTileEntity.marrowParticles(DimensionManager.getWorld(message.dimension), message.pos, message.chunkId);
		return null;
	}
}
