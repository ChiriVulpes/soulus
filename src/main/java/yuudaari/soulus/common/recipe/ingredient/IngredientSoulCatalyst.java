package yuudaari.soulus.common.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.item.SoulCatalyst;

public class IngredientSoulCatalyst extends Ingredient {

	public static IngredientSoulCatalyst INSTANCE = new IngredientSoulCatalyst();

	public IngredientSoulCatalyst () {
		super(ItemRegistry.SOUL_CATALYST.getFilledStack());
	}

	@Override
	public boolean apply (ItemStack stack) {
		return SoulCatalyst.isFilled(stack);
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			return new IngredientSoulCatalyst();
		}
	}
}
