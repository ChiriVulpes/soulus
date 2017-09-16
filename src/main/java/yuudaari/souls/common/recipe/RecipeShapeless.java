package yuudaari.souls.common.recipe;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RecipeShapeless extends Recipe {
	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = NonNullList.create();

	public RecipeShapeless(ResourceLocation group, Block result, Object... recipe) {
		this(group, new ItemStack(result), recipe);
	}

	public RecipeShapeless(ResourceLocation group, Item result, Object... recipe) {
		this(group, new ItemStack(result), recipe);
	}

	public RecipeShapeless(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result) {
		output = result.copy();
		this.group = group;
		this.input = input;
	}

	public RecipeShapeless(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
		this.group = group;
		output = result.copy();
		for (Object in : recipe) {
			Ingredient ing = CraftingHelper.getIngredient(in);
			if (ing != null) {
				input.add(ing);
			} else {
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return output;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(@Nonnull InventoryCrafting var1, @Nonnull World world) {
		NonNullList<Ingredient> required = NonNullList.create();
		required.addAll(input);

		for (int x = 0; x < var1.getSizeInventory(); x++) {
			ItemStack slot = var1.getStackInSlot(x);

			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Ingredient> req = required.iterator();

				while (req.hasNext()) {
					if (req.next().apply(slot)) {
						inRecipe = true;
						req.remove();
						break;
					}
				}

				if (!inRecipe) {
					return false;
				}
			}
		}

		return required.isEmpty();
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients() {
		return this.input;
	}

	@Override
	@Nonnull
	public String getGroup() {
		return this.group == null ? "" : this.group.toString();
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canFit(int width, int height) {
		return width * height >= this.input.size();
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			String group = JsonUtils.getString(json, "group", "");
			String override = JsonUtils.getString(json, "override", "");

			NonNullList<Ingredient> ings = NonNullList.create();
			for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
				ings.add(CraftingHelper.getIngredient(ele, context));

			if (ings.isEmpty())
				throw new JsonParseException("No ingredients for shapeless recipe");

			ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			RecipeShapeless result = new RecipeShapeless(group.isEmpty() ? null : new ResourceLocation(group), ings,
					itemstack);
			if (!override.isEmpty()) {
				result.setRegistryName(override);
			}
			return result;
		}
	}
}