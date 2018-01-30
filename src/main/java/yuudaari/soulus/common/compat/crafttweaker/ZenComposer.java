package yuudaari.soulus.common.compat.crafttweaker;

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
import java.util.List;
import java.util.Stack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.InputHelper;
import yuudaari.soulus.common.compat.crafttweaker.mtlib.LogHelper;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

@ZenClass(ZenComposer.NAME)
@ZenRegister
public class ZenComposer {

	public static final String NAME = "mods.soulus.Composer";

	@ZenMethod
	public static void addShaped (String name, IItemStack output, IIngredient[][] inputs) {
		addShaped(name, output, 1, inputs);
	}

	@ZenMethod
	public static void addShaped (String name, IItemStack output, int time, IIngredient[][] inputs) {

		Stack<Object> ingredients = new Stack<>();

		int i = 0;
		for (IIngredient[] inputArr : inputs) {
			String row = "";
			for (IIngredient input : inputArr) {
				char symbol = input == null ? ' ' : Character.forDigit(i++, 10);
				row += symbol;
				ingredients.push(symbol);
				if (input != null) {
					Ingredient ingredient = getIngredient(input);
					if (ingredient == null) return;
					ingredients.push(ingredient);
				}
			}
			ingredients.insertElementAt(row, (i - 1) / 3);
		}

		IRecipe recipe = new RecipeComposerShaped(InputHelper.toStack(output), time, ingredients
			.toArray(new Object[0]));

		recipe.setRegistryName(new ResourceLocation(name));

		ForgeRegistries.RECIPES.register(recipe);
	}

	@ZenMethod
	public static void addShapeless (String name, IItemStack output, IIngredient[] inputs) {
		addShapeless(name, output, 1, inputs);
	}

	@ZenMethod
	public static void addShapeless (String name, IItemStack output, int time, IIngredient[] inputs) {

		Object[] ingredients = new Ingredient[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			ingredients[i] = getIngredient(inputs[i]);
		}

		IRecipe recipe = new RecipeComposerShapeless(InputHelper.toStack(output), time, ingredients);

		recipe.setRegistryName(new ResourceLocation(name));

		ForgeRegistries.RECIPES.register(recipe);
	}

	private static Ingredient getIngredient (IIngredient ingredient) {
		ItemStack[] itemStacks;

		if (ingredient instanceof IOreDictEntry) {
			NonNullList<ItemStack> ores = OreDictionary.getOres(((IOreDictEntry) ingredient).getName());
			itemStacks = ores.toArray(new ItemStack[ores.size()]);

		} else if (ingredient instanceof IItemStack) {
			itemStacks = new ItemStack[] {
				(InputHelper.toStack((IItemStack) ingredient))
			};

		} else if (ingredient instanceof IngredientStack) {
			List<IItemStack> items = ingredient.getItems();
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
}
