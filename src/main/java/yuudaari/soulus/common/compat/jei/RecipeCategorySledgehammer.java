package yuudaari.soulus.common.compat.jei;

import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class RecipeCategorySledgehammer implements IRecipeCategory<IRecipeWrapper> {

	public static final ResourceLocation GUI_LOCATION = Soulus.getRegistryName("textures/gui/jei.png");
	public static final String UID = Soulus.getRegistryName("sledgehammer").toString();

	public static final int width = 162;
	public static final int height = 26;

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot = 1;
	private static final int craftSledgehammerSlot = 2;

	private final IDrawable background;
	// private final ICraftingGridHelper craftingGridHelper;

	public RecipeCategorySledgehammer (final IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(GUI_LOCATION, 0, 123, width, height);
		// craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot, craftOutputSlot);
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
	public void setRecipe (final IRecipeLayout recipeLayout, final IRecipeWrapper recipeWrapper, final IIngredients ingredients) {
		final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(craftInputSlot, true, 31, 4);
		guiItemStacks.init(craftSledgehammerSlot, true, 75, 4);
		guiItemStacks.init(craftOutputSlot, false, 109, 4);

		final List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		final List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		guiItemStacks.set(craftInputSlot, inputs.get(0));
		guiItemStacks.set(craftSledgehammerSlot, inputs.get(1));
		guiItemStacks.set(craftOutputSlot, outputs.get(0));
	}
}
