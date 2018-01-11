package yuudaari.soulus.common.compat.jei;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.util.EssenceType;

public class SubtypeInterpreterEssence implements ISubtypeInterpreter {
	@Override
	public String apply(ItemStack stack) {
		return EssenceType.getEssenceType(stack);
	}
}