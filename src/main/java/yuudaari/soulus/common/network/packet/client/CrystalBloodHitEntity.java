package yuudaari.soulus.common.network.packet.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.common.item.CrystalBlood;

public class CrystalBloodHitEntity extends EntityMessage {

	public CrystalBloodHitEntity (final EntityLivingBase entity) {
		super(entity);
	}

	public static class Handler extends EntityMessage.Handler<CrystalBloodHitEntity> {

		@Override
		public IMessage onMessage (final CrystalBloodHitEntity message, final MessageContext ctx) {
			final EntityLivingBase entity = message.getEntity();
			if (entity != null)
				CrystalBlood.bloodParticles(entity);
			return null;
		}
	}

}
