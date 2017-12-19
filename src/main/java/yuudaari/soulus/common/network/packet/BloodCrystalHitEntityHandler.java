package yuudaari.soulus.common.network.packet;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.item.BloodCrystal;

public class BloodCrystalHitEntityHandler implements IMessageHandler<BloodCrystalHitEntity, IMessage> {
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(BloodCrystalHitEntity message, MessageContext ctx) {
		BloodCrystal.particles(message.getEntity());
		return null;
	}
}