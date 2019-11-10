package yuudaari.soulus.common.compat.jei;

import java.util.Arrays;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class RecipeWrapperCrafting implements IRecipeWrapper {

	private NonNullList<Ingredient> input;
	private ItemStack output;

	public RecipeWrapperCrafting (IRecipe recipe) {
		input = recipe.getIngredients();
		output = recipe.getRecipeOutput();
	}

	@Override
	public void getIngredients (final IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input.stream()
			.map(ing -> Arrays.asList(ing.getMatchingStacks()))
			.collect(Collectors.toList()));
		ingredients.setOutput(ItemStack.class, output);
	}
}
