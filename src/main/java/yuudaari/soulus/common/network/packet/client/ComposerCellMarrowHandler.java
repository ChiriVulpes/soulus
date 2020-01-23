package yuudaari.soulus.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeAutoMarrow;

public class ComposerCellMarrowHandler implements IMessageHandler<ComposerCellMarrow, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (ComposerCellMarrow message, MessageContext ctx) {
		CellModeAutoMarrow.marrowParticles(Minecraft.getMinecraft().world, message.pos, message.chunkId, message.count);
		return null;
	}
}
