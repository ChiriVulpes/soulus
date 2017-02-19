package com.kallgirl.souls.common.item;

import com.kallgirl.souls.ModInfo;
import com.kallgirl.souls.client.ResourceMap;
import com.kallgirl.souls.client.render.IModelRegister;
import com.kallgirl.souls.common.CreativeTab;
import com.kallgirl.souls.common.IModItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class Item extends net.minecraft.item.Item implements IModItem, IModelRegister {
	Boolean glint = false;
	String name;

	public Item (String name) {
		this.name = name;
		setUnlocalizedName(name);
		setCreativeTab(CreativeTab.INSTANCE);
		GameRegistry.register(this, new ResourceLocation(ModInfo.MODID, name));
	}

	@Override
	public void preinit () {
		registerModels();
	}

	public Item (String name, Integer maxStackSize) {
		this(name);
		setMaxStackSize(maxStackSize);
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		return super.getUnlocalizedNameInefficiently(stack)
			.replaceAll("item\\.", "item." + ResourceMap.PREFIX_MOD);
	}

	@SideOnly (Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0,
			new ModelResourceLocation(getRegistryName(), "inventory")
		);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return glint;
	}
}