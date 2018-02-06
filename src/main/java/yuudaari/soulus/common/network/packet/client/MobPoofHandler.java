package yuudaari.soulus.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;

public class MobPoofHandler implements IMessageHandler<MobPoof, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (MobPoof message, MessageContext ctx) {
		ComposerTileEntity.mobPoofParticles(Minecraft.getMinecraft().world, message.getPos());
		return null;
	}
}
