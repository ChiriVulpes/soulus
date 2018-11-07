package yuudaari.soulus.common.util;

import mcjty.theoneprobe.api.IProbeHitData;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.WailaProviders;

import java.util.Collections;
import java.util.List;

public interface IBlock extends IModThing {

	abstract ResourceLocation getRegistryName ();

	abstract boolean hasItem ();

	default void registerWailaProvider (Class<? extends Block> cls) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			WailaProviders.providers.add(cls);
		}
	}

	@SideOnly(Side.CLIENT)
	default void registerItemModel () {
		ModelLoader.setCustomModelResourceLocation(this
			.getItemBlock(), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	default List<ItemBlock> getItemBlocks () {
		return Collections.singletonList(getItemBlock());
	}

	abstract void setHasItem ();

	abstract ItemBlock getItemBlock ();

	abstract boolean hasTileEntity ();

	abstract Class<? extends TileEntity> getTileEntityClass ();

	abstract CreativeTab getCreativeTabToDisplayOn ();

	abstract void getSubBlocks (CreativeTab itemIn, NonNullList<ItemStack> items);

	default List<String> getWailaTooltip (List<String> currentTooltip, IDataAccessor accessor) {
		return currentTooltip;
	}

	default ItemStack getWailaStack (IDataAccessor accessor) {
		return null;
	}

	public static interface IDataAccessor {

		TileEntity getTileEntity ();

		EntityPlayer getPlayer ();

		IBlockState getBlockState ();
	}

	public static class DataAccessorWaila implements IDataAccessor {

		private final IWailaDataAccessor accessor;

		public DataAccessorWaila (final IWailaDataAccessor accessor) {
			this.accessor = accessor;
		}

		@Override
		public TileEntity getTileEntity () {
			return accessor.getTileEntity();
		}

		@Override
		public EntityPlayer getPlayer () {
			return accessor.getPlayer();
		}

		@Override
		public IBlockState getBlockState () {
			return accessor.getBlockState();
		}
	}

	public static class DataAccessorTOP implements IDataAccessor {

		private final EntityPlayer PLAYER;
		private final IProbeHitData DATA;

		public DataAccessorTOP (final EntityPlayer player, final IProbeHitData probeHitData) {
			this.PLAYER = player;
			this.DATA = probeHitData;
		}

		@Override
		public TileEntity getTileEntity () {
			return PLAYER.getEntityWorld().getTileEntity(DATA.getPos());
		}

		@Override
		public EntityPlayer getPlayer () {
			return PLAYER;
		}

		@Override
		public IBlockState getBlockState () {
			return PLAYER.getEntityWorld().getBlockState(DATA.getPos());
		}
	}

}
