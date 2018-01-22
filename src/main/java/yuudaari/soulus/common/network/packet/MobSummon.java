package yuudaari.soulus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobSummon implements IMessage {

	public MobSummon () {}

	private int entityId;
	private float rotation;

	public MobSummon (EntityLivingBase entity) {
		entityId = entity.getEntityId();
		rotation = entity.rotationYaw;
	}

	@SideOnly(Side.CLIENT)
	public EntityLivingBase getEntity () {
		return (EntityLivingBase) Minecraft.getMinecraft().world.getEntityByID(entityId);
	}

	@SideOnly(Side.CLIENT)
	public float getRotation () {
		return rotation;
	}

	@Override
	public void toBytes (ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeFloat(rotation);
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		entityId = buf.readInt();
		rotation = buf.readFloat();
	}

}
