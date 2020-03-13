package yuudaari.soulus.common.compat.jei;

import java.util.List;
import java.util.stream.Collectors;
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
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.item.EssencePerfect;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.item.SoulCatalyst;
import yuudaari.soulus.common.recipe.RecipeShaped;
import yuudaari.soulus.common.recipe.RecipeShapeless;
import yuudaari.soulus.common.recipe.RecipeSledgehammer;
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
	public void registerItemSubtypes (final ISubtypeRegistry registry) {
		registry.registerSubtypeInterpreter(ItemRegistry.ESSENCE, EssenceType::getEssenceType);
		registry.registerSubtypeInterpreter(ItemRegistry.SOULBOOK, EssenceType::getEssenceType);
		registry.registerSubtypeInterpreter(ItemRegistry.ORB_MURKY, stack -> String.valueOf(OrbMurky.getContainedEssence(stack)));
		registry.registerSubtypeInterpreter(ItemRegistry.CRYSTAL_BLOOD, stack -> String.valueOf(CrystalBlood.getContainedBlood(stack)));
		registry.registerSubtypeInterpreter(ItemRegistry.SOUL_CATALYST, stack -> String.valueOf(SoulCatalyst.getContainedEssence(stack)));
		registry.registerSubtypeInterpreter(ItemRegistry.ESSENCE_PERFECT, stack -> EssencePerfect.getAlignment(stack)
			.getAlignments()
			.map(alignment -> alignment.getKey() + alignment.getValue())
			.sorted()
			.collect(Collectors.joining()));
	}

	@Override
	public void registerCategories (final IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryComposer(helper));
		registry.addRecipeCategories(new RecipeCategoryMarrow(helper));
		registry.addRecipeCategories(new RecipeCategorySledgehammer(helper));
	}

	@Override
	public void register (final IModRegistry registry) {
		registerCustomCraftingRecipes(registry);
		registerMarrowingRecipes(registry);
		registerHammeringRecipes(registry);
		registerComposerRecipes(registry);
		registerDescriptions(registry);
	}

	private void registerCustomCraftingRecipes (final IModRegistry registry) {
		registry.handleRecipes(RecipeShaped.class, RecipeWrapperCrafting::new, VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeShapeless.class, RecipeWrapperCrafting::new, VanillaRecipeCategoryUid.CRAFTING);
	}

	private void registerMarrowingRecipes (final IModRegistry registry) {
		ConfigEssences essences = Essence.CONFIG;
		// there are no essences to drop
		if (essences.essences.size() == 0) return;

		for (final ConfigEssence essence : essences.essences)
			if (!essence.essence.equals("NONE") && essence.bones != null)
				registry.addRecipeCatalyst(Essence.getStack(essence.essence), RecipeCategoryMarrow.UID);

		registry.handleRecipes(ConfigBoneType.class, boneType -> new RecipeWrapperMarrow(boneType, registry.getJeiHelpers().getGuiHelper()), RecipeCategoryMarrow.UID);

		registry.addRecipes(CONFIG_BONE_TYPES.boneTypes, RecipeCategoryMarrow.UID);
	}

	@SuppressWarnings("deprecation")
	private void registerHammeringRecipes (final IModRegistry registry) {
		ItemRegistry.items.stream()
			.filter(item -> (item instanceof Sledgehammer))
			.map(item -> (Sledgehammer) item)
			.forEach(sledgehammer -> registry
				.addRecipeCatalyst(sledgehammer.getItemStack(), RecipeCategorySledgehammer.UID));
		// final Tier[] tiers = Tier.values();
		// registry.addRecipeCatalyst(ItemRegistry.items
		// 	.stream()
		// 	// make the highest tier sledgehammer the catalyst for sledgehammer recipes
		// 	.filter(item -> (item instanceof Sledgehammer && ((Sledgehammer) item).tier == tiers[tiers.length - 1]))
		// 	.map(IItemRegistration::getItemStack)
		// 	.findFirst()
		// 	.orElse(ItemStack.EMPTY), RecipeCategorySledgehammer.UID);

		registry.handleRecipes(RecipeSledgehammer.class, RecipeWrapperSledgehammer::new, RecipeCategorySledgehammer.UID);

		registry.addRecipes(ForgeRegistries.RECIPES.getValues(), RecipeCategorySledgehammer.UID);
	}

	/**
	 * Registers the Composer recipe support.
	 */
	@SuppressWarnings("deprecation")
	private void registerComposerRecipes (final IModRegistry registry) {
		registry.addRecipeCatalyst(BlockRegistry.COMPOSER.getItemStack(), RecipeCategoryComposer.UID);

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
