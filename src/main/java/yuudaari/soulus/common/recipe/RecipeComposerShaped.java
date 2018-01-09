package yuudaari.soulus.common.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class RecipeComposerShaped extends Recipe {
	//Added in for future ease of change, but hard coded for now.
	public static final int MAX_CRAFT_GRID_WIDTH = 3;
	public static final int MAX_CRAFT_GRID_HEIGHT = 3;

	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = null;
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;

	public RecipeComposerShaped(Block result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public RecipeComposerShaped(Item result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public RecipeComposerShaped(@Nonnull ItemStack result, Object... recipe) {
		this(result, CraftingHelper.parseShaped(recipe));
	}

	public RecipeComposerShaped(@Nonnull ItemStack result, ShapedPrimer primer) {
		output = result.copy();
		this.width = primer.width;
		this.height = primer.height;
		this.input = primer.input;
		this.mirrored = primer.mirrored;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return output;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		if (!(inv instanceof ComposerTileEntity.ComposerContainer.CraftingMatrix))
			return false;

		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (mirrored && checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
	 */
	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Ingredient target = Ingredient.EMPTY;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}

				if (!target.apply(inv.getStackInRowAndColumn(x, y))) {
					return false;
				}
			}
		}

		return true;
	}

	public RecipeComposerShaped setMirrored(boolean mirror) {
		mirrored = mirror;
		return this;
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients() {
		return this.input;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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
		return width >= this.width && height >= this.height;
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			Map<Character, Ingredient> ingMap = Maps.newHashMap();
			for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
				if (entry.getKey().length() != 1)
					throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey()
							+ "' is an invalid symbol (must be 1 character only).");
				if (" ".equals(entry.getKey()))
					throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

				ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
			}

			ingMap.put(' ', Ingredient.EMPTY);

			JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

			if (patternJ.size() == 0)
				throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

			String[] pattern = new String[patternJ.size()];
			for (int x = 0; x < pattern.length; ++x) {
				String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
				if (x > 0 && pattern[0].length() != line.length())
					throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
				pattern[x] = line;
			}

			ShapedPrimer primer = new ShapedPrimer();
			primer.width = pattern[0].length();
			primer.height = pattern.length;
			primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
			primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

			Set<Character> keys = Sets.newHashSet(ingMap.keySet());
			keys.remove(' ');

			int x = 0;
			for (String line : pattern) {
				for (char chr : line.toCharArray()) {
					Ingredient ing = ingMap.get(chr);
					if (ing == null)
						throw new JsonSyntaxException(
								"Pattern references symbol '" + chr + "' but it's not defined in the key");
					primer.input.set(x++, ing);
					keys.remove(chr);
				}
			}

			if (!keys.isEmpty())
				throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

			ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			RecipeComposerShaped result = new RecipeComposerShaped(output, primer);

			return result;
		}
	}
}