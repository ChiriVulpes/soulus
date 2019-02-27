package yuudaari.soulus.common.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.item.OrbMurky;

public class IngredientOrbMurky extends Ingredient {

	public static IngredientOrbMurky INSTANCE = new IngredientOrbMurky();

	public IngredientOrbMurky () {
		super(ItemRegistry.ORB_MURKY.getFilledStack());
	}

	@Override
	public boolean apply (ItemStack stack) {
		return OrbMurky.isFilled(stack);
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			return new IngredientOrbMurky();
		}
	}
}
