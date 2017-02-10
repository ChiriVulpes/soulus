package com.kallgirl.souls.common;

import com.kallgirl.souls.common.block.Block;
import com.kallgirl.souls.common.item.Item;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

import com.kallgirl.souls.ModInfo;

public final class CreativeTab extends CreativeTabs {

	public static final CreativeTab INSTANCE = new CreativeTab();
	private List<ItemStack> list;

	private CreativeTab() {
		super(ModInfo.MODID);
	}

	@Nonnull
	@Override
	public ItemStack getIconItemStack() {
		return ModObjects.getItem("soulbook").getItemStack();
	}

	@Nonnull
	@Override
	public Item getTabIconItem() {
		return ModObjects.getItem("soulbook");
	}

	@Override
	public void displayAllRelevantItems(@Nonnull List<ItemStack> list) {
		this.list = list;
		ModObjects.objects.forEach((name, modObject) -> {
			if (modObject instanceof Item) {
				Item item = (Item)modObject;
				if (item.getCreativeTab() == this) {
					item.getSubItems(item, this, list);
				}
			} else if (modObject instanceof Block) {
				Block block = (Block)modObject;
				if (block.getCreativeTabToDisplayOn() == this) {
					block.getSubBlocks(block.getItemBlock(), this, list);
				}
			}
		});
	}
}