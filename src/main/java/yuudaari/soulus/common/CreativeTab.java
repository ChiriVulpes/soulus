package yuudaari.soulus.common;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;

public final class CreativeTab extends CreativeTabs {

	public static final CreativeTab INSTANCE = new CreativeTab();

	private CreativeTab () {
		super(Soulus.MODID);
	}

	@Nonnull
	@Override
	public ItemStack getTabIconItem () {
		ItemStack soulbook = ItemRegistry.SOULBOOK.getItemStack();
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

	@SuppressWarnings("unlikely-arg-type")
	private int getSortValue (ItemStack stack) {
		final Item item = stack.getItem();
		if (item instanceof ItemBlock) {
			return BlockRegistry.blocks.indexOf((Object) ((ItemBlock) item).getBlock());
		}

		return BlockRegistry.blocks.size() + ItemRegistry.items.indexOf(item);
	}
}
