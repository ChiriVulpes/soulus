package yuudaari.soulus.common.recipe;

import java.util.Random;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.recipe.ingredient.IngredientSledgehammer;

public class RecipeSledgehammer extends RecipeShapeless {

	private final Random random = new Random();
	private final Ingredient input;

	public RecipeSledgehammer (final ResourceLocation group, final Ingredient input, final ItemStack output) {
		this(group, input, output, 0);
	}

	public RecipeSledgehammer (final ResourceLocation group, final Ingredient input, final ItemStack output, final int tier) {
		super(group, output, input, new IngredientSledgehammer(tier));
		this.input = input;
	}

	public IngredientSledgehammer getSledgehammer () {
		return (IngredientSledgehammer) this.getIngredients().get(1);
	}

	public Ingredient getInput () {
		return input;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems (final InventoryCrafting inv) {
		final NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof Sledgehammer) {
				if (!stack.attemptDamageItem(1, random, null)) {
					ItemStack newStack = new ItemStack(stack.getItem());
					newStack.setItemDamage(stack.getItemDamage());
					ret.set(i, newStack);
				}
			}
		}
		return ret;
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse (final JsonContext context, final JsonObject json) {
			final String group = JsonUtils.getString(json, "group", "");
			final String override = JsonUtils.getString(json, "override", "");

			final int tier = JsonUtils.getInt(json, "tier", 0);

			final Ingredient ing = CraftingHelper.getIngredient(json.get("input"), context);

			final ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			final RecipeShapeless result = new RecipeSledgehammer(group.isEmpty() ? null : new ResourceLocation(group), ing, itemstack, tier);

			if (!override.isEmpty())
				result.setRegistryName(override);

			return result;
		}
	}
}
