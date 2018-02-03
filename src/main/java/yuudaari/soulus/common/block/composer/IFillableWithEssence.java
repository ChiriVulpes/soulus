package yuudaari.soulus.common.block.composer;

import net.minecraft.item.ItemStack;

public interface IFillableWithEssence {

	public int fill (ItemStack currentStack, ItemStack fillWith, int quantity);

	public float getFillPercentage (ItemStack stack);
}
