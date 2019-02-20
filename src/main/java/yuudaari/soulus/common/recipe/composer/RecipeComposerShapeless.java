package yuudaari.soulus.common.recipe.composer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;
import yuudaari.soulus.common.recipe.Recipe;
import yuudaari.soulus.common.recipe.RecipeUtils;
import yuudaari.soulus.common.util.JSON;

public class RecipeComposerShapeless extends Recipe implements IRecipeComposer {

	protected final ItemStack output;
	protected final NonNullList<Ingredient> input = NonNullList.create();
	protected final float time;
	protected final Map<String, Integer> mobsRequired = new HashMap<>();
	protected final Set<String> mobWhitelist = new HashSet<>();
	protected final Set<String> mobBlacklist = new HashSet<>();

	public RecipeComposerShapeless (final ItemStack result, final float time, final Map<String, Integer> mobsRequired, final Set<String> mobWhitelist, final Set<String> mobBlacklist, final Object... recipe) {
		output = result.copy();
		this.time = time;
		if (mobsRequired != null) this.mobsRequired.putAll(mobsRequired);
		if (mobWhitelist != null) this.mobWhitelist.addAll(mobWhitelist);
		if (mobBlacklist != null) this.mobBlacklist.addAll(mobBlacklist);

		Arrays.stream(recipe)
			.map(in -> CraftingHelper.getIngredient(in))
			.forEach(ing -> {
				if (ing == null)
					throw new RuntimeException("Invalid shapeless ore recipe: " + Arrays.stream(recipe)
						.map(obj -> obj.toString())
						.collect(Collectors.joining(", ")));
				input.add(ing);
			});
	}

	public float getTime () {
		return time;
	}

	@Override
	public Map<String, Integer> getMobsRequired () {
		return mobsRequired;
	}

	@Override
	public Set<String> getMobBlacklist () {
		return mobBlacklist;
	}

	@Override
	public Set<String> getMobWhitelist () {
		return mobWhitelist;
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
		public IRecipe parse (final JsonContext context, final JsonObject json) {

			final Object[] ingredients = Streams.stream(JsonUtils.getJsonArray(json, "ingredients"))
				.map(ing -> CraftingHelper.getIngredient(ing, context))
				.toArray(Ingredient[]::new);

			if (ingredients.length == 0)
				throw new JsonParseException("No ingredients for shapeless recipe");

			final ItemStack output = RecipeUtils.getOutput(json.get("result"), context);

			final float time = JsonUtils.getFloat(json, "time", 1);

			final JsonObject mobs = JsonUtils.getJsonObject(json, "mobs_required", null);
			final Map<String, Integer> requiredMobs = mobs == null ? null : mobs.getAsJsonObject()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAsInt()));

			final Set<String> whitelist = JSON.getStringSet(json, "mob_whitelist");
			final Set<String> blacklist = JSON.getStringSet(json, "mob_blacklist");

			return new RecipeComposerShapeless(output, time, requiredMobs, whitelist, blacklist, ingredients);
		}
	}
}
