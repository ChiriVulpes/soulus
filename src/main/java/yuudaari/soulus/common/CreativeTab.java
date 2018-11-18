package yuudaari.soulus.common;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.item.Soulbook;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import javax.annotation.Nonnull;

public final class CreativeTab extends CreativeTabs {

	public static final CreativeTab INSTANCE = new CreativeTab();

	private CreativeTab () {
		super(Soulus.MODID);
	}

	@Nonnull
	@Override
	public ItemStack getTabIconItem () {
		ItemStack soulbook = ModItems.SOULBOOK.getItemStack();
		Soulbook.setContainedEssence(soulbook, 1);
		return soulbook;
	}

	@Override
	public void displayAllRelevantItems (NonNullList<ItemStack> list) {
		super.displayAllRelevantItems(list);

		list.sort( (item1, item2) -> {
			return getSortValue(item1) - getSortValue(item2);
		});
	}

	private int getSortValue (ItemStack stack) {
		return stack.getItem() instanceof ItemBlock ? 0 : 1;
	}
}
