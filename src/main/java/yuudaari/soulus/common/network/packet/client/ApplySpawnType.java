package yuudaari.soulus.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.common.misc.NoMobSpawning;
import yuudaari.soulus.common.misc.SpawnType;

public class ApplySpawnType extends EntityMessage {

	private SpawnType spawnType;

	public ApplySpawnType () {
	}

	public ApplySpawnType (final SpawnType spawnType, final EntityLivingBase entity) {
		super(entity);
		this.spawnType = spawnType;
	}

	@Override
	public void toBytes (final ByteBuf buf) {
		super.toBytes(buf);
		buf.writeByte(spawnType.ordinal());
	}

	@Override
	public void fromBytes (final ByteBuf buf) {
		super.fromBytes(buf);
		spawnType = SpawnType.values()[buf.readByte()];
	}

	public static class Handler extends EntityMessage.Handler<ApplySpawnType> {

		@Override
		public IMessage onMessage (final ApplySpawnType message, final MessageContext ctx) {
			final EntityLivingBase entity = message.getEntity();
			if (entity != null)
				message.spawnType.apply(entity, false);
			else
				NoMobSpawning.registerSpawnType(message.entityId, message.spawnType);
			return null;
		}
	}
}
