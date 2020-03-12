package yuudaari.soulus.common.network.packet.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.common.block.skewer.SkewerTileEntity;

public class TetherEntity extends EntityMessage {

	public TetherEntity (final EntityLivingBase entity) {
		super(entity);
	}

	public static class Handler extends EntityMessage.Handler<TetherEntity> {

		@Override
		public IMessage onMessage (final TetherEntity message, final MessageContext ctx) {
			final EntityLivingBase entity = message.getEntity();
			if (entity != null)
				SkewerTileEntity.tetherEntity(entity);
			return null;
		}
	}

}
