package yuudaari.soulus.common.block.composer;

import net.minecraft.item.ItemStack;

public interface IFillableWithEssence {

	public int fillWithEssence (final ItemStack currentStack, final ItemStack fillWith, final int quantity);

	public float getEssenceFillPercentage (final ItemStack stack);

	public boolean isFilledWithEssence (final ItemStack stack);
}
