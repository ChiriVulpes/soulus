package yuudaari.soulus.common.recipe.ingredient;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.EssenceConfig;
import yuudaari.soulus.common.item.Essence;

public class IngredientPotentialEssence extends Ingredient {

	public static IngredientPotentialEssence INSTANCE = new IngredientPotentialEssence();

	public static ItemStack[] getMatchingStacks1() {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(ItemStack.EMPTY);
		for (EssenceConfig essenceConfig : Soulus.config.essences) {
			stacks.add(Essence.getStack(essenceConfig.essence));
		}
		return stacks.toArray(new ItemStack[0]);
	}

	public IngredientPotentialEssence() {
		super(getMatchingStacks1());
	}

	@Override
	public boolean apply(ItemStack input) {
		return input == null || input.isEmpty() || input.getItem() == ModItems.ESSENCE
				|| input.getItem() == ModItems.ASH;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	public static class Factory implements IIngredientFactory {
		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			return new IngredientPotentialEssence();
		}
	}
}