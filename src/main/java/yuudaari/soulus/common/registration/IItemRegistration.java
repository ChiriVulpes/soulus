package yuudaari.soulus.common.registration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.item.IRightClickableItem;

public interface IItemRegistration extends IRegistration<Item>, IRightClickableItem {

	abstract void getSubItems (final CreativeTabs tab, final NonNullList<ItemStack> items);

	@Override
	default Item getItem () {
		return (Item) this;
	}

	@SideOnly(Side.CLIENT)
	default void registerColorHandler (IItemColor itemColor) {
		Soulus.onInit( (FMLInitializationEvent event) -> {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, (Item) this);
		});
	}

}
