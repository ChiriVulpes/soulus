package yuudaari.soulus.common.network.packet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.item.CrystalBlood;

public class CrystalBloodHitEntityHandler implements IMessageHandler<CrystalBloodHitEntity, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (CrystalBloodHitEntity message, MessageContext ctx) {
		EntityLivingBase entity = message.getEntity();
		if (entity != null) {
			CrystalBlood.bloodParticles(entity);
		}
		return null;
	}
}
