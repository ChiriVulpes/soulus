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
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.InputHelper;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.LogHelper;
import yuudaari.soulus.common.recipe.IRecipeComposer;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

@ZenClass(ZenComposer.NAME)
@ZenRegister
public class ZenComposer {

	public static final String NAME = "mods.soulus.Composer";

	@ZenMethod
	public static void addShaped (final String name, final IItemStack output, final IIngredient[][] inputs) {
		addShaped(name, output, 1, inputs);
	}

	@ZenMethod
	public static void addShaped (final String name, final IItemStack output, final float time, final IIngredient[][] inputs) {

		final Stack<Object> ingredients = new Stack<>();

		int i = 0;
		for (final IIngredient[] inputArr : inputs) {
			String row = "";
			for (final IIngredient input : inputArr) {
				final char symbol = input == null ? ' ' : Character.forDigit(i++, 10);
				row += symbol;
				if (symbol == ' ' || input == null) continue;

				ingredients.push(symbol);
				final Ingredient ingredient = getIngredient(input);
				if (ingredient == null) return;
				ingredients.push(ingredient);
			}
			ingredients.insertElementAt(row, (i - 1) / 3);
		}

		final IRecipe recipe = new RecipeComposerShaped(InputHelper.toStack(output), time, ingredients
			.toArray(new Object[0]));

		recipe.setRegistryName(new ResourceLocation(name));

		CraftTweakerAPI.apply(new Add(recipe));
	}

	@ZenMethod
	public static void addShapeless (final String name, final IItemStack output, final IIngredient[] inputs) {
		addShapeless(name, output, 1, inputs);
	}

	@ZenMethod
	public static void addShapeless (final String name, final IItemStack output, final float time, final IIngredient[] inputs) {

		final Object[] ingredients = new Ingredient[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			ingredients[i] = getIngredient(inputs[i]);
		}

		final IRecipe recipe = new RecipeComposerShapeless(InputHelper.toStack(output), time, ingredients);

		recipe.setRegistryName(new ResourceLocation(name));

		CraftTweakerAPI.apply(new Add(recipe));
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

	public static class Add implements IAction {

		private final IRecipe recipe;

		public Add (final IRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public String describe () {
			return "Adding Composer recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public void apply () {
			ForgeRegistries.RECIPES.register(recipe);
		}
	}


	@ZenMethod
	public static void remove (final String name) {
		CraftTweakerAPI.apply(new Remove(name));
	}

	@ZenMethod
	public static void remove (final IItemStack output) {
		CraftTweakerAPI.apply(new Remove(InputHelper.toStack(output)));
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
