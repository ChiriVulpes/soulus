package yuudaari.souls.common.util;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import yuudaari.souls.Souls;

public final class Recipes {
	public static void register(String name, IRecipeFactory factory) {
		Souls.logger.info(Souls.getRegistryName(name) + factory.toString());
		CraftingHelper.register(Souls.getRegistryName(name), factory);
	}
}