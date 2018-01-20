package yuudaari.soulus.common.network.packet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.skewer.SkewerTileEntity;

public class TetherEntityHandler implements IMessageHandler<TetherEntity, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (TetherEntity message, MessageContext ctx) {
		EntityLivingBase entity = message.getEntity();
		if (entity != null) {
			SkewerTileEntity.tetherEntity(entity);
		}
		return null;
	}
}
