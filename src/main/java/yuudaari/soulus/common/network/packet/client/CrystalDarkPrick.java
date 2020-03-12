package yuudaari.soulus.common.network.packet.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.common.item.CrystalDark;

public class CrystalDarkPrick extends EntityMessage {

	public CrystalDarkPrick (final EntityLivingBase entity) {
		super(entity);
	}

	public static class Handler extends EntityMessage.Handler<CrystalDarkPrick> {

		@Override
		public IMessage onMessage (final CrystalDarkPrick message, final MessageContext ctx) {
			final EntityLivingBase entity = message.getEntity();
			if (entity != null)
				CrystalDark.particles(entity);
			return null;
		}
	}

}
