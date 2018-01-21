package yuudaari.soulus.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Iterator;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;

public class RecipeComposerShapeless extends Recipe implements IRecipeComposer {

	@Nonnull protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = NonNullList.create();
	protected float time = 1;

	public float getTime () {
		return time;
	}

	public RecipeComposerShapeless (Block result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public RecipeComposerShapeless (Item result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public RecipeComposerShapeless (NonNullList<Ingredient> input, @Nonnull ItemStack result) {
		output = result.copy();
		this.input = input;
	}

	public RecipeComposerShapeless (@Nonnull ItemStack result, Object... recipe) {
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
	public ItemStack getRecipeOutput () {
		return output;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	@Nonnull
	public ItemStack getCraftingResult (@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches (@Nonnull InventoryCrafting var1, @Nonnull World world) {
		if (!(var1 instanceof ComposerTileEntity.ComposerContainer.CraftingMatrix))
			return false;

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
	public NonNullList<Ingredient> getIngredients () {
		return this.input;
	}

	@Override
	@Nonnull
	public String getGroup () {
		return this.group == null ? "" : this.group.toString();
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canFit (int width, int height) {
		return width * height >= this.input.size();
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse (JsonContext context, JsonObject json) {
			NonNullList<Ingredient> ings = NonNullList.create();
			for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
				ings.add(CraftingHelper.getIngredient(ele, context));

			if (ings.isEmpty())
				throw new JsonParseException("No ingredients for shapeless recipe");

			ItemStack output = RecipeUtils.getOutput(json.get("result"), context);
			RecipeComposerShapeless result = new RecipeComposerShapeless(ings, output);

			result.time = JsonUtils.getFloat(json, "time", result.time);

			return result;
		}
	}
}
