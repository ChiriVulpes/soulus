package yuudaari.soulus.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.ModItems;

public class IngredientPotentialEssence extends Ingredient {

	//public static IngredientPotentialEssence INSTANCE = new IngredientPotentialEssence();

	@Override
	public boolean apply(ItemStack input) {
		return input == null || input.isEmpty() || input.getItem() == ModItems.ESSENCE
				|| input.getItem() == ModItems.ASH;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	public static class Factory implements IIngredientFactory {
		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			return new IngredientPotentialEssence();
		}
	}
}