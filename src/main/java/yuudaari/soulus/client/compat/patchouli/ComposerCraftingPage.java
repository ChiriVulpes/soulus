package yuudaari.soulus.client.compat.patchouli;

import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;
import yuudaari.soulus.common.recipe.IRecipeComposer;
import yuudaari.soulus.common.util.LangHelper;

public class ComposerCraftingPage implements IComponentProcessor {

	private transient IRecipe recipe;
	private transient @Nullable IRecipe recipe2;
	private transient String text;
	private transient @Nullable String title;
	private transient @Nullable String title2;

	@Override
	public void setup (IVariableProvider<String> provider) {
		text = !provider.has("text") ? "" : provider.get("text");
		recipe = getRecipe(provider.get("recipe"));
		recipe2 = !provider.has("recipe2") ? null : getRecipe(provider.get("recipe2"));

		title = recipe.getRecipeOutput().getDisplayName();
		if (provider.has("title")) {
			title = provider.get("title");

		} else {
			title2 = recipe2 == null ? null : recipe2.getRecipeOutput().getDisplayName();
			if (title.equalsIgnoreCase(title2)) title2 = null;
		}
	}

	@Override
	public String process (String key) {
		return key.startsWith("recipe2_") ? processRecipe(recipe2, key.substring(8)) : processRecipe(recipe, key);
	}

	@Override
	public boolean allowRender (String group) {
		switch (group) {
			case "single_recipe":
				return recipe2 == null;
			case "multi_recipe":
				return recipe2 != null && title2 != null;
			case "title2":
				return title2 != null;
			case "multi_recipe_no_title2":
				return recipe2 != null && title2 == null;
			case "shapeless":
				return !(recipe instanceof IShapedRecipe);
			case "recipe2_shapeless":
				return recipe2 != null && title2 != null && !(recipe2 instanceof IShapedRecipe);
			case "recipe2_shapeless_no_title2":
				return recipe2 != null && title2 == null && !(recipe2 instanceof IShapedRecipe);
		}

		return false;
	}

	private String processRecipe (IRecipe recipe, String key) {
		if (recipe == null) return null;

		if (key.startsWith("input")) {
			return processRecipeInput(recipe, Integer.parseInt(key.substring(5)) - 1);
		}

		switch (key) {
			case "output":
				return PatchouliAPI.instance.serializeItemStack(recipe.getRecipeOutput());
			case "time": {
				String timeString = "" + (recipe instanceof IRecipeComposer ? ((IRecipeComposer) recipe).getTime() : 1);
				if (timeString.endsWith(".0"))
					timeString = timeString.substring(0, timeString.length() - 2);
				timeString = LangHelper.localize("patchouli.recipe.soulus:composer.recipe_time", timeString);
				return timeString;
			}
			case "name":
				return recipe != recipe2 || title2 == null ? title : title2;
			case "text":
				return text;
			default:
				return null;
		}
	}

	private String processRecipeInput (IRecipe recipe, int ingredientId) {
		NonNullList<Ingredient> ingredients = recipe.getIngredients();

		if (ingredients.size() > ingredientId)
			return PatchouliAPI.instance.serializeIngredient(ingredients.get(ingredientId));
		else
			return null;
	}

	private IRecipe getRecipe (String name) {
		return ForgeRegistries.RECIPES.getValue(new ResourceLocation(name));
	}


}
