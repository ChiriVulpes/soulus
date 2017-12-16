package yuudaari.soulus.common.compat;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.Summoner.Summoner;
import yuudaari.soulus.common.block.Summoner.SummonerTileEntity;

@WailaPlugin(Soulus.MODID)
public class Waila implements IWailaPlugin, IWailaDataProvider {
	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.registerHeadProvider(this, Summoner.class);
		registrar.registerBodyProvider(this, Summoner.class);
		registrar.registerStackProvider(this, Summoner.class);
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
	
		if (accessor.getBlock() instanceof Summoner) {
			SummonerTileEntity summoner = (SummonerTileEntity) accessor.getTileEntity();
			return summoner.getWailaHeader(currenttip);
		}
	
		return currenttip;
	}
	*/

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {

		if (accessor.getBlock() instanceof Summoner) {
			SummonerTileEntity summoner = (SummonerTileEntity) accessor.getTileEntity();
			return summoner.getWailaTooltip(currenttip);
		}

		return currenttip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (accessor.getBlock() instanceof Summoner) {
			return ((Summoner) accessor.getBlock()).getItemStack((SummonerTileEntity) accessor.getTileEntity());
		}

		return null;
	}
}