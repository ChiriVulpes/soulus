package yuudaari.souls.common.compat;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.souls.Souls;
import yuudaari.souls.common.block.Summoner.Summoner;
import yuudaari.souls.common.block.Summoner.SummonerTileEntity;

@WailaPlugin(Souls.MODID)
public class Waila implements IWailaPlugin, IWailaDataProvider {
	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.registerHeadProvider(this, Summoner.class);
		registrar.registerBodyProvider(this, Summoner.class);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {

		if (accessor.getBlock() instanceof Summoner) {
			SummonerTileEntity summoner = (SummonerTileEntity) accessor.getTileEntity();
			return summoner.getWailaHeader();
		}

		return currenttip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {

		if (accessor.getBlock() instanceof Summoner) {
			SummonerTileEntity summoner = (SummonerTileEntity) accessor.getTileEntity();
			return summoner.getWailaTooltip();
		}

		return currenttip;
	}
}