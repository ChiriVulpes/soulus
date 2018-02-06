package yuudaari.soulus.common.network.packet.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.item.CrystalDark;

public class CrystalDarkPrickHandler implements IMessageHandler<CrystalDarkPrick, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (CrystalDarkPrick message, MessageContext ctx) {
		EntityLivingBase entity = message.getEntity();
		if (entity != null) {
			CrystalDark.particles(entity);
		}
		return null;
	}
}
