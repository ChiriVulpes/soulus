package yuudaari.soulus.common.recipe.composer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;
import yuudaari.soulus.common.recipe.Recipe;
import yuudaari.soulus.common.recipe.RecipeUtils;
import yuudaari.soulus.common.util.JSON;
import yuudaari.soulus.common.util.Logger;

public class RecipeComposerShaped extends Recipe implements IRecipeComposer, IShapedRecipe {

	// Added in for future ease of change, but hard coded for now.
	public static final int MAX_CRAFT_GRID_WIDTH = 3;
	public static final int MAX_CRAFT_GRID_HEIGHT = 3;

	@Nonnull protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = null;
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;
	protected final float time;
	protected final Map<String, Integer> mobsRequired = new HashMap<>();
	protected final Set<String> mobWhitelist = new HashSet<>();
	protected final Set<String> mobBlacklist = new HashSet<>();

	public RecipeComposerShaped (final ItemStack result, final float time, final Map<String, Integer> mobsRequired, final Set<String> mobWhitelist, final Set<String> mobBlacklist, final Object... recipe) {
		this(result, time, mobsRequired, mobWhitelist, mobBlacklist, CraftingHelper.parseShaped(recipe));
	}

	private RecipeComposerShaped (final ItemStack result, final float time, final Map<String, Integer> requiredMobs, final Set<String> whitelist, final Set<String> blacklist, final ShapedPrimer primer) {
		output = result.copy();
		this.width = primer.width;
		this.height = primer.height;
		this.input = primer.input;
		this.mirrored = primer.mirrored;
		this.time = time;
		if (requiredMobs != null) this.mobsRequired.putAll(requiredMobs);
		if (whitelist != null) this.mobWhitelist.addAll(whitelist);
		if (blacklist != null) this.mobBlacklist.addAll(blacklist);
	}

	public float getTime () {
		return time;
	}

	@Override
	public Map<String, Integer> getMobsRequired () {
		return mobsRequired;
	}

	@Override
	public Set<String> getMobWhitelist () {
		return mobWhitelist;
	}

	@Override
	public Set<String> getMobBlacklist () {
		return mobBlacklist;
	}

	@Override
	public int getRecipeWidth () {
		return width;
	}

	@Override
	public int getRecipeHeight () {
		return height;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	@Nonnull
	public ItemStack getCraftingResult (@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput () {
		return output;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches (@Nonnull InventoryCrafting inv, @Nonnull World world) {
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
	protected boolean checkMatch (InventoryCrafting inv, int startX, int startY, boolean mirror) {
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

	public RecipeComposerShaped setMirrored (boolean mirror) {
		mirrored = mirror;
		return this;
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients () {
		return this.input;
	}

	public int getWidth () {
		return width;
	}

	public int getHeight () {
		return height;
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
		return width >= this.width && height >= this.height;
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse (final JsonContext context, final JsonObject json) {
			final Map<Character, Ingredient> ingMap = Maps.newHashMap();
			for (final Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
				if (entry.getKey().length() != 1)
					throw new JsonSyntaxException("Invalid key entry: '" + entry
						.getKey() + "' is an invalid symbol (must be 1 character only).");
				if (" ".equals(entry.getKey()))
					throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

				ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
			}

			ingMap.put(' ', Ingredient.EMPTY);

			final JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

			if (patternJ.size() == 0)
				throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

			final String[] pattern = new String[patternJ.size()];
			for (int x = 0; x < pattern.length; ++x) {
				final String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
				if (x > 0 && pattern[0].length() != line.length())
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				pattern[x] = line;
			}

			final ShapedPrimer primer = new ShapedPrimer();
			primer.width = pattern[0].length();
			primer.height = pattern.length;
			primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
			primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

			final Set<Character> keys = Sets.newHashSet(ingMap.keySet());
			keys.remove(' ');

			int x = 0;
			for (final String line : pattern) {
				for (final char chr : line.toCharArray()) {
					final Ingredient ing = ingMap.get(chr);
					if (ing == null)
						throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
					primer.input.set(x++, ing);
					keys.remove(chr);
				}
			}

			if (!keys.isEmpty())
				Logger.warn("Key defines symbols that aren't used in pattern: " + keys);

			final ItemStack output = RecipeUtils.getOutput(json.get("result"), context);

			final float time = JsonUtils.getFloat(json, "time", 1);

			final JsonObject mobs = JsonUtils.getJsonObject(json, "mobs_required", null);
			final Map<String, Integer> requiredMobs = mobs == null ? null : mobs.getAsJsonObject()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAsInt()));

			final Set<String> whitelist = JSON.getStringSet(json, "mob_whitelist");
			final Set<String> blacklist = JSON.getStringSet(json, "mob_blacklist");

			return new RecipeComposerShaped(output, time, requiredMobs, whitelist, blacklist, primer);
		}
	}
}
