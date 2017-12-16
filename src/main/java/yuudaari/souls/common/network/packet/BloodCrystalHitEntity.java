package yuudaari.souls.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.souls.common.item.BloodCrystal;

public class BloodCrystalHitEntity implements IMessage {
	public BloodCrystalHitEntity() {
	}

	private int entityId;

	public BloodCrystalHitEntity(EntityLivingBase entity) {
		entityId = entity.getEntityId();
	}

	public EntityLivingBase getEntity() {
		return (EntityLivingBase) Minecraft.getMinecraft().world.getEntityByID(entityId);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
	}

	public static class Handler implements IMessageHandler<BloodCrystalHitEntity, IMessage> {
		@Override
		public IMessage onMessage(BloodCrystalHitEntity message, MessageContext ctx) {
			BloodCrystal.particles(message.getEntity());
			return null;
		}
	}
}