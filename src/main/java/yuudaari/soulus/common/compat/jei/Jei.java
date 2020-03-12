package yuudaari.soulus.common.compat.jei;

import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.Tuple2;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.misc.ConfigModSupport;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.recipe.RecipeShaped;
import yuudaari.soulus.common.recipe.RecipeShapeless;
import yuudaari.soulus.common.recipe.composer.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.composer.RecipeComposerShapeless;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.IBlockRegistration;
import yuudaari.soulus.common.registration.IItemRegistration;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.EssenceType;

@JEIPlugin
@ConfigInjected(Soulus.MODID)
public class Jei implements IModPlugin {

	@Inject public static ConfigBoneTypes CONFIG_BONE_TYPES;
	@Inject public static ConfigModSupport CONFIG_MOD_SUPPORT;

	@Override
	public void registerItemSubtypes (ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.ESSENCE, EssenceType::getEssenceType);
	}

	@Override
	public void registerCategories (IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryComposer(helper));
		registry.addRecipeCategories(new RecipeCategoryMarrow(helper));
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

		for (final ConfigEssence essence : essences.essences)
			if (!essence.essence.equals("NONE") && essence.bones != null)
				registry.addRecipeCatalyst(Essence.getStack(essence.essence), RecipeCategoryMarrow.UID);

		registry.handleRecipes(ConfigBoneType.class, boneType -> new RecipeWrapperMarrow(boneType, registry.getJeiHelpers().getGuiHelper()), RecipeCategoryMarrow.UID);

		registry.addRecipes(CONFIG_BONE_TYPES.boneTypes, RecipeCategoryMarrow.UID);
	}

	/**
	 * Registers the Composer recipe support.
	 */
	@SuppressWarnings("deprecation")
	private void registerComposer (IModRegistry registry) {
		registry.addRecipeCatalyst(BlockRegistry.COMPOSER.getItemStack(), RecipeCategoryComposer.UID);

		registry.handleRecipes(RecipeShaped.class, RecipeWrapperCrafting::new, VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeShapeless.class, RecipeWrapperCrafting::new, VanillaRecipeCategoryUid.CRAFTING);

		registry.handleRecipes(RecipeComposerShaped.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(RecipeComposerShapeless.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);

		if (CONFIG_MOD_SUPPORT.jei.showNormalRecipesInComposerTab) {
			registry.handleRecipes(ShapedOreRecipe.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
			registry.handleRecipes(ShapedRecipes.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
			registry.handleRecipes(ShapelessOreRecipe.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
			registry.handleRecipes(ShapelessRecipes.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		}

		registry.addRecipes(ForgeRegistries.RECIPES.getValues(), RecipeCategoryComposer.UID);
	}

	/**
	 * Registers the descriptions for the mod items.
	 */
	private void registerDescriptions (final IModRegistry registry) {
		final JeiDescriptionRegistry descriptionRegistry = new JeiDescriptionRegistry();

		for (final IItemRegistration item : ItemRegistry.items)
			item.onRegisterDescription(descriptionRegistry);

		for (final IBlockRegistration<?> block : BlockRegistry.blocks)
			block.onRegisterDescription(descriptionRegistry);

		for (final Tuple2<List<ItemStack>, String> description : descriptionRegistry.ingredients)
			registry.addIngredientInfo(description._1(), ItemStack.class, "jei.description." + description._2());
	}

}
