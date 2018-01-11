package yuudaari.soulus.common.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

public class RecipeWrapperComposer implements IRecipeWrapper {

	private List<List<ItemStack>> inputs;
	private ItemStack output;

	private boolean isShaped;

	public int getWidth () {
		return isShaped ? 3 : 0;
	}

	public int getHeight () {
		return isShaped ? 3 : 0;
	}

	public boolean isShaped () {
		return isShaped;
	}

	private ResourceLocation registryName;

	public ResourceLocation getRegistryName () {
		return registryName;
	}

	public RecipeWrapperComposer (RecipeComposerShaped recipe) {
		inputs = new ArrayList<>();

		for (Ingredient input : recipe.getIngredients()) {
			inputs.add(Arrays.asList(input.getMatchingStacks()));
		}

		output = recipe.getRecipeOutput();

		isShaped = true;

		registryName = recipe.getRegistryName();
	}

	public RecipeWrapperComposer (RecipeComposerShapeless recipe) {
		inputs = new ArrayList<>();

		for (Ingredient input : recipe.getIngredients()) {
			inputs.add(Arrays.asList(input.getMatchingStacks()));
		}

		output = recipe.getRecipeOutput();

		isShaped = false;

		registryName = recipe.getRegistryName();
	}

	@Override
	public void getIngredients (IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}
}
