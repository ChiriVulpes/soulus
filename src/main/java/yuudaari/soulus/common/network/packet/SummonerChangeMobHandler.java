package yuudaari.soulus.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.summoner.SummonerTileEntity;
import yuudaari.soulus.common.util.Logger;

public class SummonerChangeMobHandler implements IMessageHandler<SummonerChangeMob, IMessage> {

	@SideOnly(Side.CLIENT)
	public SummonerTileEntity getTileEntity(SummonerChangeMob m) {
		return (SummonerTileEntity) Minecraft.getMinecraft().world.getTileEntity(m.pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(SummonerChangeMob message, MessageContext ctx) {
		Logger.info("change mob handled");
		SummonerTileEntity summoner = getTileEntity(message);
		summoner.setMob(message.mob);
		summoner.reset();
		return null;
	}
}