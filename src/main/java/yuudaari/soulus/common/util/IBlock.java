package yuudaari.soulus.common.util;

import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.WailaProviders;
import yuudaari.soulus.common.util.IModItem;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlock extends IModItem {
	abstract ResourceLocation getRegistryName();

	abstract boolean hasItem();

	default void registerWailaProvider(Class<? extends Block> cls) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			WailaProviders.providers.add(cls);
		}
	}

	@SideOnly(Side.CLIENT)
	default void registerItemModel() {
		ModelLoader.setCustomModelResourceLocation(this.getItemBlock(), 0,
				new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	default List<ItemBlock> getItemBlocks() {
		return Collections.singletonList(getItemBlock());
	}

	abstract void setHasItem();

	abstract ItemBlock getItemBlock();

	abstract boolean hasTileEntity();

	abstract Class<? extends TileEntity> getTileEntityClass();

	abstract CreativeTab getCreativeTabToDisplayOn();

	abstract void getSubBlocks(CreativeTab itemIn, NonNullList<ItemStack> items);

	@SideOnly(Side.CLIENT)
	default List<String> getWailaTooltip(List<String> currentTooltip, WailaProviders.Accessor accessor) {
		return currentTooltip;
	}

	@SideOnly(Side.CLIENT)
	default ItemStack getWailaStack(WailaProviders.Accessor accessor) {
		return null;
	}
}