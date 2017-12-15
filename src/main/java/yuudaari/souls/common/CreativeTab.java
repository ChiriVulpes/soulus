package yuudaari.souls.common;

import yuudaari.souls.Souls;
import yuudaari.souls.common.item.Soulbook;
import yuudaari.souls.common.util.IBlock;
import yuudaari.souls.common.util.ModItem;
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
		ItemStack soulbook = ModItems.SOULBOOK.getItemStack();
		Soulbook.setContainedEssence(soulbook, 1);
		return soulbook;
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> list) {
		for (IBlock block : ModBlocks.blocks) {
			if (block.getCreativeTabToDisplayOn() == this) {
				block.getSubBlocks(this, list);
			}
		}
		for (ModItem item : ModItems.items) {
			if (item.getCreativeTab() == this) {
				item.getSubItems(this, list);
			}
		}
	}
}