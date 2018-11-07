package yuudaari.soulus.common.compat;

import java.util.List;
import java.util.Random;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.Tuple2;
import yuudaari.soulus.common.compat.jei.RecipeCategoryComposer;
import yuudaari.soulus.common.compat.jei.RecipeCategoryEssence;
import yuudaari.soulus.common.compat.jei.RecipeWrapperComposer;
import yuudaari.soulus.common.compat.jei.RecipeWrapperEssence;
import yuudaari.soulus.common.compat.jei.SubtypeInterpreterEssence;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

@JEIPlugin
@ConfigInjected(Soulus.MODID)
public class Jei implements IModPlugin {

	@Inject public static ConfigBoneTypes CONFIG;

	@Override
	public void registerItemSubtypes (ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(ModItems.ESSENCE, new SubtypeInterpreterEssence());
	}

	@Override
	public void registerCategories (IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryComposer(helper));
		registry.addRecipeCategories(new RecipeCategoryEssence(helper));
	}

	@Override
	public void register (IModRegistry registry) {
		registerDescriptions(registry);
		registerComposer(registry);
		registerEssenceDrops(registry);
	}

	private void registerEssenceDrops (IModRegistry registry) {
		ConfigEssences essences = Essence.CONFIG;
		// there are no essences to drop
		if (essences.essences.size() == 0) return;

		// make the icon a random essence
		ConfigEssence essence = null;
		for (int i = 0; i < 100; i++) {
			essence = essences.essences.get(new Random().nextInt(essences.essences.size()));
			if (!essence.essence.equals("NONE")) break;
		}
		registry.addRecipeCatalyst(Essence.getStack(essence.essence), RecipeCategoryEssence.UID);

		registry.handleRecipes(ConfigBoneType.class, RecipeWrapperEssence::new, RecipeCategoryEssence.UID);

		registry.addRecipes(CONFIG.boneTypes, RecipeCategoryEssence.UID);
	}

	/**
	 * Registers the Composer recipe support.
	 */
	private void registerComposer (IModRegistry registry) {
		registry.addRecipeCatalyst(ModBlocks.COMPOSER.getItemStack(), RecipeCategoryComposer.UID);

		registry.handleRecipes(RecipeComposerShaped.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(RecipeComposerShapeless.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapedOreRecipe.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapedRecipes.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapelessOreRecipe.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapelessRecipes.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);

		registry.addRecipes(ForgeRegistries.RECIPES.getValues(), RecipeCategoryComposer.UID);
	}

	/**
	 * Registers the descriptions for the mod items.
	 */
	private void registerDescriptions (IModRegistry registry) {
		JeiDescriptionRegistry descriptionRegistry = new JeiDescriptionRegistry();

		ModItems.registerDescriptions(descriptionRegistry);
		ModBlocks.registerDescriptions(descriptionRegistry);

		for (Tuple2<List<ItemStack>, String> description : descriptionRegistry.ingredients) {
			registry.addIngredientInfo(description._1(), ItemStack.class, "jei.description." + description._2());
		}
	}

}
