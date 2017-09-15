package yuudaari.souls.common;

import yuudaari.souls.Souls;
import yuudaari.souls.common.block.SoulsBlock;
import yuudaari.souls.common.item.SoulsItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import javax.annotation.Nonnull;

public final class CreativeTab extends CreativeTabs {

	public static final CreativeTab INSTANCE = new CreativeTab();

	private CreativeTab() {
		super(Souls.MODID);
	}

	@Nonnull
	@Override
	public ItemStack getTabIconItem() {
		return ModObjects.getItem("soulbook").getItemStack();
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> list) {
		ModObjects.objects.forEach((name, modObject) -> {
			if (modObject instanceof SoulsItem) {
				SoulsItem item = (SoulsItem) modObject;
				if (item.getCreativeTab() == this) {
					item.getSubItems(this, list);
				}
			} else if (modObject instanceof SoulsBlock) {
				SoulsBlock block = (SoulsBlock) modObject;
				if (block.getCreativeTabToDisplayOn() == this) {
					block.getSubBlocks(this, list);
				}
			}
		});
	}
}