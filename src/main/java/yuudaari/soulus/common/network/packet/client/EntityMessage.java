package yuudaari.soulus.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityMessage implements IMessage {

	public EntityMessage () {
	}

	public int entityId;

	public EntityMessage (final EntityLivingBase entity) {
		entityId = entity.getEntityId();
	}

	@SideOnly(Side.CLIENT)
	public EntityLivingBase getEntity () {
		return (EntityLivingBase) Minecraft.getMinecraft().world.getEntityByID(entityId);
	}

	@Override
	public void toBytes (final ByteBuf buf) {
		buf.writeInt(entityId);
	}

	@Override
	public void fromBytes (final ByteBuf buf) {
		entityId = buf.readInt();
	}

	public static abstract class Handler<T extends EntityMessage> implements IMessageHandler<T, IMessage> {
	}
}
