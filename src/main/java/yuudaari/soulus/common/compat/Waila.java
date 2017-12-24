package yuudaari.soulus.common.compat;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.IBlock;

@WailaPlugin(Soulus.MODID)
public class Waila implements IWailaPlugin, IWailaDataProvider {

	@SideOnly(Side.CLIENT)
	public static class Accessor {
		private IWailaDataAccessor accessor;

		public Accessor(IWailaDataAccessor accessor) {
			this.accessor = accessor;
		}

		public TileEntity getTileEntity() {
			return accessor.getTileEntity();
		}

		public EntityPlayer getPlayer() {
			return accessor.getPlayer();
		}

		public int getMetadata() {
			return accessor.getMetadata();
		}
	}

	public static List<Class<? extends Block>> providers = new ArrayList<>();

	@Override
	public void register(IWailaRegistrar registrar) {
		for (Class<? extends Block> cls : providers) {
			registrar.registerBodyProvider(this, cls);
			registrar.registerStackProvider(this, cls);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {

		Block block = accessor.getBlock();
		if (block != null && block instanceof IBlock) {
			return ((IBlock) block).getWailaTooltip(currenttip, new Accessor(accessor));
		}

		return currenttip;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		ItemStack result = null;

		Block block = accessor.getBlock();
		if (block != null && block instanceof IBlock) {
			result = ((IBlock) block).getWailaStack(new Accessor(accessor));
		}

		return result == null ? new ItemStack(block, 1, accessor.getMetadata()) : result;
	}

}