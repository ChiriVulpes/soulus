package yuudaari.soulus.common.registration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcjty.theoneprobe.api.IProbeHitData;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.WailaProviders;

public interface IBlockRegistration extends IRegistration<Block> {

	abstract Block getBlock ();

	////////////////////////////////////
	// Items
	//

	// I would make this private if I could
	public static final Map<IBlockRegistration, ItemBlock> BLOCKS_WITH_ITEMS = new HashMap<>();

	default boolean hasItem () {
		return BLOCKS_WITH_ITEMS.containsKey(this);
	}

	@Override
	default Item getItem () {
		return getItemBlock();
	}

	default IBlockRegistration setHasItem () {
		BLOCKS_WITH_ITEMS.put(this, null);
		return this;
	}

	default ItemBlock getItemBlock () {
		if (!BLOCKS_WITH_ITEMS.containsKey(this)) {
			throw new IllegalArgumentException("This block has no registered item");
		}

		ItemBlock result = BLOCKS_WITH_ITEMS.get(this);
		if (result == null) {
			result = createItemBlock();
			BLOCKS_WITH_ITEMS.put(this, result);
		}

		return result;
	}

	default ItemBlock createItemBlock () {
		return new Registration.ItemBlock(this);
	}

	default List<ItemBlock> getItemBlocks () {
		return Collections.singletonList(getItemBlock());
	}

	@SideOnly(Side.CLIENT)
	default void registerItemModel () {
		ModelLoader.setCustomModelResourceLocation(getItemBlock(), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	abstract void getSubBlocks (final CreativeTabs itemIn, final NonNullList<ItemStack> items);

	default NonNullList<ItemStack> getSubBlocks () {
		final NonNullList<ItemStack> items = NonNullList.create();
		getSubBlocks(CreativeTab.INSTANCE, items);
		return items;
	}

	////////////////////////////////////
	// Misc
	//

	default Class<? extends TileEntity> getTileEntityClass () {
		return null;
	}

	////////////////////////////////////
	// WAILA/TOP support
	//

	default void registerWailaProvider (final Class<? extends Block> cls) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			WailaProviders.providers.add(cls);
		}
	}

	default List<String> getWailaTooltip (final List<String> currentTooltip, final IDataAccessor accessor) {
		return currentTooltip;
	}

	default ItemStack getWailaStack (final IDataAccessor accessor) {
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
