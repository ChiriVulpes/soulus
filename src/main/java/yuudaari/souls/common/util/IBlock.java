package yuudaari.souls.common.util;

import yuudaari.souls.common.util.IModItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public interface IBlock extends IModItem {
	abstract ResourceLocation getRegistryName();

	abstract boolean hasItem();

	abstract void setHasItem();

	abstract ItemBlock getItemBlock();

	abstract boolean hasTileEntity();

	abstract Class<? extends TileEntity> getTileEntityClass();

	abstract CreativeTabs getCreativeTabToDisplayOn();

	abstract void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items);
}