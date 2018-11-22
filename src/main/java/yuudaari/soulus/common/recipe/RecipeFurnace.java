package yuudaari.soulus.common.recipe;

import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeFurnace implements IRecipe {

	private ResourceLocation name;
	private final Ingredient input;
	private final ItemStack output;

	public RecipeFurnace (final Ingredient input, final ItemStack output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public NonNullList<Ingredient> getIngredients () {
		return NonNullList.from(input, input);
	}

	public boolean canFit (int width, int height) {
		return true;
	}

	public RecipeFurnace setRegistryName (ResourceLocation name) {
		this.name = name;
		return this;
	}

	public ResourceLocation getRegistryName () {
		return name;
	}

	public ItemStack getRecipeOutput () {
		return this.output;
	}

	public boolean matches (InventoryCrafting inv, World world) {
		return false;
	}

	public ItemStack getCraftingResult (InventoryCrafting inv) {
		return this.output;
	}

	public Class<IRecipe> getRegistryType () {
		return IRecipe.class;
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse (JsonContext context, JsonObject json) {
			if (json.has("override")) {
				// remove all furnace recipes that match 
				Ingredient ing = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "override"), context);
				List<ItemStack> toRemove = new ArrayList<>();
				for (Map.Entry<ItemStack, ItemStack> recipe : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
					if (ing.apply(recipe.getKey())) {
						toRemove.add(recipe.getKey());
					}
				}
				for (ItemStack recipeInput : toRemove) {
					FurnaceRecipes.instance().getSmeltingList().remove(recipeInput);
				}
			}

			JsonElement ele = JsonUtils.getJsonObject(json, "input");
			Ingredient ing = CraftingHelper.getIngredient(ele, context);

			ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			for (ItemStack input : ing.getMatchingStacks()) {
				FurnaceRecipes.instance().getSmeltingList().put(input, output);
			}

			return new RecipeFurnace(ing, output);
		}
	}
}
