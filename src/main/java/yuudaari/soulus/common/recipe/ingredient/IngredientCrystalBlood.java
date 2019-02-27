package yuudaari.soulus.common.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.item.CrystalBlood;

public class IngredientCrystalBlood extends Ingredient {

	public static IngredientCrystalBlood INSTANCE = new IngredientCrystalBlood();

	public IngredientCrystalBlood () {
		super(ItemRegistry.CRYSTAL_BLOOD.getFilledStack());
	}

	@Override
	public boolean apply (ItemStack stack) {
		return CrystalBlood.isFilled(stack);
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			return new IngredientCrystalBlood();
		}
	}
}
