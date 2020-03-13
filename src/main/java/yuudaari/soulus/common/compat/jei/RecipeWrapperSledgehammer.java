package yuudaari.soulus.common.compat.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.recipe.RecipeSledgehammer;
import yuudaari.soulus.common.util.RegionI;
import yuudaari.soulus.common.util.Translation;

public class RecipeWrapperSledgehammer implements IRecipeWrapper {

	private ItemStack output;
	private List<List<ItemStack>> inputs;

	private ResourceLocation registryName;

	public RecipeWrapperSledgehammer (final RecipeSledgehammer recipe) {
		inputs = Stream.of(recipe.getInput(), recipe.getSledgehammer())
			.map(Ingredient::getMatchingStacks)
			.map(Arrays::stream)
			.map(stream -> stream.collect(Collectors.toList()))
			.collect(Collectors.toList());

		output = recipe.getRecipeOutput();
	}

	public ResourceLocation getRegistryName () {
		return registryName;
	}

	@Override
	public void getIngredients (final IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	@Override
	public List<String> getTooltipStrings (int mouseX, int mouseY) {
		if (!new RegionI(53, 4, 18, 18).isPosWithin(mouseX, mouseY))
			return Collections.emptyList();

		return Collections.singletonList(Translation.localize("jei.recipe.soulus:sledgehammer.requires_machine_hammering_tooltip"));
	}
}
