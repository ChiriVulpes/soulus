package yuudaari.soulus.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.config.Constants;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.Translation;
import java.util.List;

public class RecipeCategoryComposer implements IRecipeCategory<IRecipeWrapper> {

	public static final String UID = "soulus:composer";

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static final int width = 116;
	public static final int height = 54;

	private final IDrawable background;
	private final ICraftingGridHelper craftingGridHelper;

	public RecipeCategoryComposer (IGuiHelper guiHelper) {
		ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
		background = guiHelper.createDrawable(location, 0, 60, width, height);
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
	}

	@Override
	public String getUid () {
		return UID;
	}

	@Override
	public String getTitle () {
		return Translation.localize("jei.recipe." + getUid() + ".name");
	}

	@Override
	public String getModName () {
		return Soulus.MODID;
	}

	@Override
	public IDrawable getBackground () {
		return background;
	}

	@Override
	public void setRecipe (IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(craftOutputSlot, false, 94, 18);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				int index = craftInputSlot1 + x + (y * 3);
				guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}

		if (recipeWrapper instanceof ICustomCraftingRecipeWrapper) {
			ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper) recipeWrapper;
			customWrapper.setRecipe(recipeLayout, ingredients);
			return;
		}

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		if (recipeWrapper instanceof IShapedCraftingRecipeWrapper) {
			IShapedCraftingRecipeWrapper wrapper = (IShapedCraftingRecipeWrapper) recipeWrapper;
			craftingGridHelper.setInputs(guiItemStacks, inputs, wrapper.getWidth(), wrapper.getHeight());
		} else if (recipeWrapper instanceof RecipeWrapperComposer && ((RecipeWrapperComposer) recipeWrapper)
			.isShaped()) {
			RecipeWrapperComposer wrapper = (RecipeWrapperComposer) recipeWrapper;
			craftingGridHelper.setInputs(guiItemStacks, inputs, wrapper.getWidth(), wrapper.getHeight());
		} else {
			craftingGridHelper.setInputs(guiItemStacks, inputs);
			recipeLayout.setShapeless();
		}
		guiItemStacks.set(craftOutputSlot, outputs.get(0));

		ResourceLocation registryName = null;

		if (recipeWrapper instanceof ICraftingRecipeWrapper) {
			ICraftingRecipeWrapper craftingRecipeWrapper = (ICraftingRecipeWrapper) recipeWrapper;
			registryName = craftingRecipeWrapper.getRegistryName();

		} else if (recipeWrapper instanceof RecipeWrapperComposer) {
			RecipeWrapperComposer craftingRecipeWrapper = (RecipeWrapperComposer) recipeWrapper;
			registryName = craftingRecipeWrapper.getRegistryName();
		}

		if (registryName != null) {
			final ResourceLocation actualRegistryName = registryName;
			guiItemStacks.addTooltipCallback( (slotIndex, input, ingredient, tooltip) -> {
				if (slotIndex == craftOutputSlot) {
					String recipeModId = actualRegistryName.getResourceDomain();

					boolean modIdDifferent = false;
					ResourceLocation itemRegistryName = ingredient.getItem().getRegistryName();
					if (itemRegistryName != null) {
						String itemModId = itemRegistryName.getResourceDomain();
						modIdDifferent = !recipeModId.equals(itemModId);
					}

					if (modIdDifferent) {
						String modName = ForgeModIdHelper.getInstance().getFormattedModNameForModId(recipeModId);
						tooltip.add(TextFormatting.GRAY + Translator
							.translateToLocalFormatted("jei.tooltip.recipe.by", modName));
					}

					boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen
						.isShiftKeyDown();
					if (showAdvanced) {
						tooltip.add(TextFormatting.GRAY + actualRegistryName.getResourcePath());
					}
				}
			});
		}
	}

}
