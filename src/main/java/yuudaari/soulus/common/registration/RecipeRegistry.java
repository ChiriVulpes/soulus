package yuudaari.soulus.common.registration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.recipe.RecipeShaped;
import yuudaari.soulus.common.recipe.RecipeShapeless;
import yuudaari.soulus.common.recipe.composer.IRecipeComposer;
import yuudaari.soulus.common.recipe.composer.SpawnEggRecipe;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
@ConfigInjected(Soulus.MODID)
public class RecipeRegistry {

	@Inject public static ConfigEssences CONFIG_ESSENCES;

	@SubscribeEvent(priority = EventPriority.LOW) // run after recipe registration from items and blocks
	public static void registerRecipes (final RegistryEvent.Register<IRecipe> event) {
		deregisterInvalidComposerRecipes(event);
	}

	/**
	 * Remove invalid composer recipes after they're registered
	 */
	private static void deregisterInvalidComposerRecipes (final RegistryEvent.Register<IRecipe> event) {
		final IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();

		final List<ResourceLocation> toRemove = new ArrayList<>();
		for (final IRecipe recipe : registry) {
			final boolean isSoulusRecipe = recipe instanceof IRecipeComposer //
				|| recipe instanceof RecipeShaped //
				|| recipe instanceof RecipeShapeless //
				|| recipe.getRegistryName().getResourceDomain().equalsIgnoreCase(Soulus.MODID);

			if (isSoulusRecipe && recipe.getRecipeOutput().isEmpty())
				toRemove.add(recipe.getRegistryName());
		}

		for (final ResourceLocation name : toRemove)
			registry.remove(name);
	}
}
