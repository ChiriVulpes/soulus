package yuudaari.soulus.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.google.common.collect.Sets;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.InputHelper;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.LogHelper;
import yuudaari.soulus.common.recipe.composer.IRecipeComposer;
import yuudaari.soulus.common.recipe.composer.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.composer.RecipeComposerShapeless;

@ZenClass(ZenComposer.NAME)
@ZenRegister
public class ZenComposer {

	public static final String NAME = "mods.soulus.Composer";

	private static final List<IAction> REMOVALS = new ArrayList<>();
	private static final List<IAction> ADDITIONS = new ArrayList<>();

	public static void apply () {

		for (IAction removal : REMOVALS) {
			CraftTweakerAPI.apply(removal);
		}

		for (IAction addition : ADDITIONS) {
			CraftTweakerAPI.apply(addition);
		}
	}

	@ZenMethod
	public static Factory recipe (final String name, final IItemStack output) {
		return new Factory(name, output);
	}

	public static class Factory implements IZenComposerFactory, IAction {

		public final String name;
		public final IItemStack output;
		public float time = 1;
		public IIngredient[] inputsShapeless = null;
		public IIngredient[][] inputsShaped = null;
		public Set<String> mobWhitelist = null;
		public Set<String> mobBlacklist = null;
		public Map<String, Integer> mobsRequired = null;

		private Factory (final String name, final IItemStack output) {
			this.name = name;
			this.output = output;
		}

		@Override
		public Factory setTime (final float time) {
			this.time = time;
			return this;
		}

		@Override
		public Factory setShaped (final IIngredient[][] inputsShaped) {
			this.inputsShapeless = null;
			this.inputsShaped = inputsShaped;
			return this;
		}

		@Override
		public Factory setShapeless (final IIngredient[] inputsShapeless) {
			this.inputsShaped = null;
			this.inputsShapeless = inputsShapeless;
			return this;
		}

		@Override
		public Factory setMobWhitelist (final String[] mobWhitelist) {
			this.mobWhitelist = Sets.newHashSet(mobWhitelist);
			return this;
		}

		@Override
		public Factory setMobBlacklist (final String[] mobBlacklist) {
			this.mobBlacklist = Sets.newHashSet(mobBlacklist);
			return this;
		}

		@Override
		public Factory setMobsRequired (final Map<String, Integer> mobsRequired) {
			this.mobsRequired = mobsRequired;
			return this;
		}

		@Override
		public String describe () {
			return "Adding Composer recipe '" + name + "'. Output: " + output.getDisplayName();
		}

		@Override
		public void create () {
			ADDITIONS.add(this);
		}

		@Override
		public void apply () {

			// Delay the resolution of ingredients (specifically ore dict ingredients)
			// until the last possible moment to ensure mods and scripts have a chance
			// to get their entries in before retrieving entries from the dictionary
			if (inputsShapeless == null && inputsShaped == null) {
				LogHelper.logError("No ingredients set for recipe: '" + name + "'");
				return;
			}

			final IRecipe recipe = (inputsShaped == null ? createShapeless() : createShaped())
				.setRegistryName(new ResourceLocation(name));

			ForgeRegistries.RECIPES.register(recipe);
		}

		private RecipeComposerShaped createShaped () {
			final Stack<Object> ingredients = new Stack<>();

			int i = 0;
			for (final IIngredient[] inputArr : inputsShaped) {
				String row = "";
				for (final IIngredient input : inputArr) {
					final char symbol = input == null ? ' ' : Character.forDigit(i++, 10);
					row += symbol;
					if (symbol == ' ' || input == null) continue;

					ingredients.push(symbol);
					final Ingredient ingredient = getIngredient(input);
					if (ingredient == null) {
						LogHelper.logError("Unknown ingredient for symbol '" + symbol + "' in recipe '" + name + "'");
						return null;
					}
					ingredients.push(ingredient);
				}
				ingredients.insertElementAt(row, (i - 1) / 3);
			}

			return new RecipeComposerShaped(InputHelper.toStack(output), time, mobsRequired, mobWhitelist, mobBlacklist, ingredients.toArray(new Object[0]));
		}

		private RecipeComposerShapeless createShapeless () {
			final Object[] ingredients = new Ingredient[inputsShapeless.length];
			for (int i = 0; i < inputsShapeless.length; i++) {
				ingredients[i] = getIngredient(inputsShapeless[i]);
			}

			return new RecipeComposerShapeless(InputHelper.toStack(output), time, mobsRequired, mobWhitelist, mobBlacklist, ingredients);
		}
	}

	private static Ingredient getIngredient (final IIngredient ingredient) {
		ItemStack[] itemStacks;

		if (ingredient instanceof IOreDictEntry) {
			final NonNullList<ItemStack> ores = OreDictionary.getOres(((IOreDictEntry) ingredient).getName());
			itemStacks = ores.toArray(new ItemStack[ores.size()]);

		} else if (ingredient instanceof IItemStack) {
			itemStacks = new ItemStack[] {
				(InputHelper.toStack((IItemStack) ingredient))
			};

		} else if (ingredient instanceof IngredientStack) {
			final List<IItemStack> items = ingredient.getItems();
			itemStacks = new ItemStack[items.size()];

			for (int j = 0; j < items.size(); j++) {
				itemStacks[j] = InputHelper.toStack(items.get(j));
			}

		} else {
			LogHelper.logError("Unknown input type: " + ingredient);
			return null;
		}

		return Ingredient.fromStacks(itemStacks);
	}

	@ZenMethod
	public static void remove (final String name) {
		REMOVALS.add(new Remove(name));
	}

	@ZenMethod
	public static void remove (final IItemStack output) {
		REMOVALS.add(new Remove(InputHelper.toStack(output)));
	}

	public static class Remove implements IAction {

		private final Object find;

		public Remove (final String name) {
			find = name;
		}

		public Remove (final ItemStack stack) {
			find = stack;
		}

		@Override
		public String describe () {
			return "Removing composer recipe for " + find;
		}

		@Override
		public void apply () {
			final IForgeRegistryModifiable<IRecipe> recipes = (IForgeRegistryModifiable<IRecipe>) ForgeRegistries.RECIPES;

			if (find instanceof ItemStack) {

				final List<Entry<ResourceLocation, IRecipe>> matchingRecipes = recipes.getEntries()
					.stream()
					.filter(r -> r.getValue() instanceof IRecipeComposer && //
						ItemStack.areItemStacksEqual(r.getValue().getRecipeOutput(), (ItemStack) find))
					.collect(Collectors.toList());

				for (final Entry<ResourceLocation, IRecipe> entry : matchingRecipes) {
					recipes.remove(entry.getKey());
				}

			} else {
				recipes.remove(new ResourceLocation((String) find));
			}
		}
	}

}
