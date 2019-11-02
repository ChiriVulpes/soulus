package yuudaari.soulus.common.recipe;

import java.util.Random;
import com.google.gson.JsonElement;
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
import yuudaari.soulus.common.registration.ItemRegistry;

public class RecipeSledgehammer extends RecipeShapeless {

	private final Random random = new Random();

	public RecipeSledgehammer (final ResourceLocation group, final Ingredient input, final ItemStack output) {
		super(group, output, input, ItemRegistry.SLEDGEHAMMER);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems (final InventoryCrafting inv) {
		final NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ItemRegistry.SLEDGEHAMMER) {
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

			final JsonElement ele = JsonUtils.getJsonObject(json, "input");
			final Ingredient ing = CraftingHelper.getIngredient(ele, context);

			final ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			final RecipeShapeless result = new RecipeSledgehammer(group.isEmpty() ? null : new ResourceLocation(group), ing, itemstack);

			if (!override.isEmpty())
				result.setRegistryName(override);

			return result;
		}
	}
}
