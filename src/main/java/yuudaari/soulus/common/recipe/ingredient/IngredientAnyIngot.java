package yuudaari.soulus.common.recipe.ingredient;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientAnyIngot extends Ingredient {

	public static ItemStack[] getMatchingStacks1 () {
		List<ItemStack> stacks = new ArrayList<>();

		stacks.addAll(OreDictionary.getOres("ingotIron"));
		stacks.addAll(OreDictionary.getOres("ingotGold"));
		stacks.addAll(OreDictionary.getOres("ingotCopper"));
		stacks.addAll(OreDictionary.getOres("ingotTin"));
		stacks.addAll(OreDictionary.getOres("ingotBrass"));
		stacks.addAll(OreDictionary.getOres("ingotBronze"));
		stacks.addAll(OreDictionary.getOres("ingotOsmium"));
		stacks.addAll(OreDictionary.getOres("ingotLead"));
		stacks.addAll(OreDictionary.getOres("ingotTitanium"));
		stacks.addAll(OreDictionary.getOres("ingotAluminum"));
		stacks.addAll(OreDictionary.getOres("ingotSilver"));
		stacks.addAll(OreDictionary.getOres("ingotPlatinum"));
		stacks.addAll(OreDictionary.getOres("ingotShiny"));
		stacks.addAll(OreDictionary.getOres("ingotThaumium"));
		stacks.addAll(OreDictionary.getOres("ingotZinc"));
		stacks.addAll(OreDictionary.getOres("ingotSteel"));
		stacks.addAll(OreDictionary.getOres("ingotRedstone"));
		stacks.addAll(OreDictionary.getOres("ingotElectrum"));
		stacks.addAll(OreDictionary.getOres("ingotTungsten"));
		stacks.addAll(OreDictionary.getOres("ingotUranium"));
		stacks.addAll(OreDictionary.getOres("ingotBrick"));
		stacks.addAll(OreDictionary.getOres("ingotBrickNether"));

		return stacks.toArray(new ItemStack[0]);
	}

	public IngredientAnyIngot () {
		super(getMatchingStacks1());
	}

	@Override
	public boolean apply (ItemStack input) {
		if (input.isEmpty()) return false;

		int[] ores = OreDictionary.getOreIDs(input);
		for (int oreId : ores) {
			if (OreDictionary.getOreName(oreId).startsWith("ingot")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			return new IngredientAnyIngot();
		}
	}
}
