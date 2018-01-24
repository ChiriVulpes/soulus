package yuudaari.soulus.common.compat.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

public class RecipeWrapperComposer implements IRecipeWrapper {

	private List<List<ItemStack>> inputs;
	private ItemStack output;

	private boolean isShaped;
	private float recipeTime = 1;

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

	public RecipeWrapperComposer (IRecipe recipe, boolean isShaped) {
		inputs = new ArrayList<>();
		for (Ingredient input : recipe.getIngredients()) {
			inputs.add(Arrays.asList(input.getMatchingStacks()));
		}

		output = recipe.getRecipeOutput();
		registryName = recipe.getRegistryName();

		this.isShaped = isShaped;
	}

	public RecipeWrapperComposer (RecipeComposerShaped recipe) {
		this(recipe, true);
		recipeTime = recipe.getTime();
	}

	public RecipeWrapperComposer (RecipeComposerShapeless recipe) {
		this(recipe, false);
		registryName = recipe.getRegistryName();
	}

	@Override
	public void getIngredients (IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	@Override
	public void drawInfo (Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		String timeString = "" + recipeTime;
		if (timeString.endsWith(".0"))
			timeString = timeString.substring(0, timeString.length() - 2);
		String renderString = I18n.format("jei.recipe.soulus:composer.recipe_time", timeString);
		int stringWidth = minecraft.fontRenderer.getStringWidth(renderString);
		minecraft.fontRenderer.drawString(renderString, 72 - stringWidth / 2, 9, Color.DARK_GRAY.getRGB(), false);
	}
}
