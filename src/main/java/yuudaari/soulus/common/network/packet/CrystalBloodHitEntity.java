package yuudaari.soulus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrystalBloodHitEntity implements IMessage {
	public CrystalBloodHitEntity() {
	}

	private int entityId;

	public CrystalBloodHitEntity(EntityLivingBase entity) {
		entityId = entity.getEntityId();
	}

	@SideOnly(Side.CLIENT)
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

}