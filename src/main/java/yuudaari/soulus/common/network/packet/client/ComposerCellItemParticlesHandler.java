package yuudaari.soulus.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;

public class ComposerCellItemParticlesHandler implements IMessageHandler<ComposerCellItemParticles, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (ComposerCellItemParticles message, MessageContext ctx) {
		ComposerCellTileEntity.itemParticles(Minecraft.getMinecraft().world, message.pos, message.chunkId, message.count);
		return null;
	}
}
