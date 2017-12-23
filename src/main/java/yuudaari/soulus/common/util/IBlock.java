package yuudaari.soulus.common.util;

import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.util.IModItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlock extends IModItem {
	abstract ResourceLocation getRegistryName();

	abstract boolean hasItem();

	@SideOnly(Side.CLIENT)
	default void registerItemModel() {
		ModelLoader.setCustomModelResourceLocation(this.getItemBlock(), 0,
				new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	abstract void setHasItem();

	abstract ItemBlock getItemBlock();

	abstract boolean hasTileEntity();

	abstract Class<? extends TileEntity> getTileEntityClass();

	abstract CreativeTab getCreativeTabToDisplayOn();

	abstract void getSubBlocks(CreativeTab itemIn, NonNullList<ItemStack> items);
}