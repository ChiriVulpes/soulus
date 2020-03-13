package yuudaari.soulus.common.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.registration.ItemRegistry;

public class IngredientSledgehammer extends Ingredient {

	public IngredientSledgehammer (final int tier) {
		super(ItemRegistry.items.stream()
			.filter(item -> (item instanceof Sledgehammer))
			.map(item -> (Sledgehammer) item)
			.sorted( (a, b) -> a.tier.ordinal() - b.tier.ordinal())
			.skip(tier)
			.map(ItemStack::new)
			.map(stack -> {
				stack.setItemDamage(OreDictionary.WILDCARD_VALUE);
				return stack;
			})
			.toArray(ItemStack[]::new));
	}
}
