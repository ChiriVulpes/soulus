package yuudaari.soulus.common.compat;

import java.util.List;
import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.IBlockRegistration;

@WailaPlugin(Soulus.MODID)
public class Waila implements IWailaPlugin, IWailaDataProvider {

	@Override
	public void register (IWailaRegistrar registrar) {
		for (Class<? extends Block> cls : WailaProviders.providers) {
			registrar.registerBodyProvider(this, cls);
			registrar.registerStackProvider(this, cls);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		Block block = accessor.getBlock();
		if (block != null && block instanceof IBlockRegistration) {
			return ((IBlockRegistration) block).getWailaTooltip(currenttip, new IBlockRegistration.DataAccessorWaila(accessor));
		}

		return currenttip;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config) {
		ItemStack result = null;

		Block block = accessor.getBlock();
		if (block != null && block instanceof IBlockRegistration) {
			result = ((IBlockRegistration) block).getWailaStack(new IBlockRegistration.DataAccessorWaila(accessor));
		}

		return result == null ? new ItemStack(block, 1, accessor.getMetadata()) : result;
	}

}
