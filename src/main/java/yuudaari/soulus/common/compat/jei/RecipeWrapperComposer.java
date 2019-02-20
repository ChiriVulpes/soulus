package yuudaari.soulus.common.compat.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import yuudaari.soulus.common.recipe.composer.IRecipeComposer;
import yuudaari.soulus.common.recipe.composer.RecipeComposerShaped;
import yuudaari.soulus.common.util.RegionI;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Vec2i;

public class RecipeWrapperComposer implements IRecipeWrapper {

	private List<List<ItemStack>> inputs;
	private ItemStack output;

	private boolean isShaped = false;
	private float recipeTime = 1;
	private Map<String, Integer> requiredMobs;

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

	public RecipeWrapperComposer (IRecipe recipe) {
		inputs = new ArrayList<>();
		for (Ingredient input : recipe.getIngredients()) {
			inputs.add(Arrays.asList(input.getMatchingStacks()));
		}

		output = recipe.getRecipeOutput();
		registryName = recipe.getRegistryName();

		if (recipe instanceof IRecipeComposer) {
			final IRecipeComposer composerRecipe = (IRecipeComposer) recipe;
			recipeTime = composerRecipe.getTime();
			requiredMobs = composerRecipe.getMobsRequired();
		}

		if (recipe instanceof RecipeComposerShaped || recipe instanceof ShapedOreRecipe || recipe instanceof ShapedRecipes) {
			isShaped = true;
		}
	}

	@Override
	public void getIngredients (IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	@Override
	public void drawInfo (Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		final String time = getTimeString();
		final RegionI regionTime = getTimeRegion(time);
		minecraft.fontRenderer.drawString(time, regionTime.pos.x, regionTime.pos.y, Color.DARK_GRAY.getRGB(), false);

		if (requiredMobs != null && requiredMobs.size() > 0) {
			final String mobs = getMobsString();
			final RegionI regionMobs = getMobsRegion(mobs);
			minecraft.fontRenderer.drawString(mobs, regionMobs.pos.x, regionMobs.pos.y, Color.DARK_GRAY.getRGB(), false);
		}
	}

	@Override
	public List<String> getTooltipStrings (int mouseX, int mouseY) {
		if (getTimeRegion().isPosWithin(new Vec2i(mouseX, mouseY)))
			return Collections.singletonList(Translation.localize("jei.recipe.soulus:composer.recipe_time_tooltip"));

		if (requiredMobs != null && requiredMobs.size() > 0 && getMobsRegion().isPosWithin(new Vec2i(mouseX, mouseY)))
			return requiredMobs.entrySet()
				.stream()
				.map(requiredMob -> new Translation("waila.soulus:composer.required_creature")
					.get(Translation.localizeEntity(requiredMob.getKey()), requiredMob.getValue()))
				.collect(Collectors.toList());

		return IRecipeWrapper.super.getTooltipStrings(mouseX, mouseY);
	}

	private String getTimeString () {
		String timeString = "" + recipeTime;
		if (timeString.endsWith(".0"))
			timeString = timeString.substring(0, timeString.length() - 2);
		return Translation.localize("jei.recipe.soulus:composer.recipe_time", timeString);
	}

	private RegionI getTimeRegion () {
		return getTimeRegion(getTimeString());
	}

	private RegionI getTimeRegion (final String timeString) {
		final FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		final int stringWidth = renderer.getStringWidth(timeString);
		return new RegionI(new Vec2i(72 - stringWidth / 2, 9), new Vec2i(stringWidth, renderer.FONT_HEIGHT));
	}

	private String getMobsString () {
		return Translation.localize("jei.recipe.soulus:composer.mobs_required");
	}

	private RegionI getMobsRegion () {
		return getMobsRegion(getMobsString());
	}

	private RegionI getMobsRegion (final String mobsString) {
		final FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		final int stringWidth = renderer.getStringWidth(mobsString);
		return new RegionI(new Vec2i(116 - stringWidth, 46), new Vec2i(stringWidth, renderer.FONT_HEIGHT));
	}
}
