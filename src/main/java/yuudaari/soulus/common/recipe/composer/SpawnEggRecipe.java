package yuudaari.soulus.common.recipe.composer;

import java.util.Collections;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.recipe.ingredient.IngredientNBTSensitive;

public class SpawnEggRecipe extends RecipeComposerShapeless {

	private static ItemStack getSpawnEggStack (final String essenceType) {
		final ItemStack result = new ItemStack(Items.SPAWN_EGG);
		ItemMonsterPlacer.applyEntityIdToItemStack(result, new ResourceLocation(essenceType));
		return result;
	}

	public SpawnEggRecipe (final String essenceType) {
		super(getSpawnEggStack(essenceType), 8, ImmutableMap.of(essenceType, 64), Collections.emptySet(), Collections.emptySet(), //
			Stream.concat(Stream.of(Items.EGG), Collections.nCopies(8, new IngredientNBTSensitive(Essence.getStack(essenceType))).stream())
				.toArray(Object[]::new));
		setRegistryName(Soulus.getRegistryName("egg" + "_" + essenceType.replace(":", "_")));
	}
}
