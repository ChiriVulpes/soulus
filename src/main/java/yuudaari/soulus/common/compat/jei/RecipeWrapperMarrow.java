package yuudaari.soulus.common.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.RegionI;
import yuudaari.soulus.common.util.Translation;

public class RecipeWrapperMarrow implements IRecipeWrapper {

	private List<ItemStack> outputs;
	private List<ItemStack> inputs;
	private boolean requiresMachineMarrowing;
	private final IDrawable machineMarrowingDisplay;

	private static final RegionI machineMarrowingRegion = new RegionI(99, 4, 39, 18);
	private static final RegionI machineMarrowingRegionCell = new RegionI(machineMarrowingRegion.x() - 1, machineMarrowingRegion.y(), machineMarrowingRegion.h(), machineMarrowingRegion.h());

	private ResourceLocation registryName;

	public RecipeWrapperMarrow (final ConfigBoneType boneType, final IGuiHelper guiHelper) {
		machineMarrowingDisplay = guiHelper.createDrawable(RecipeCategoryMarrow.GUI_LOCATION, 0, 105, machineMarrowingRegion.w(), machineMarrowingRegion.h());

		requiresMachineMarrowing = !boneType.canBeMarrowedManually;

		inputs = new ArrayList<>();
		inputs.add(boneType.getChunkStack());
		if (requiresMachineMarrowing) {
			inputs.add(ItemRegistry.GEAR_OSCILLATING.getItemStack());
			inputs.add(BlockRegistry.COMPOSER_CELL.getItemStack());
		}

		outputs = new ArrayList<>();
		outputs = Essence.getStacksFromBoneType(boneType);
	}

	public ResourceLocation getRegistryName () {
		return registryName;
	}

	public boolean requiresMachineMarrowing () {
		return requiresMachineMarrowing;
	}

	@Override
	public void getIngredients (final IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo (final Minecraft minecraft, final int recipeWidth, final int recipeHeight, final int mouseX, final int mouseY) {
		if (requiresMachineMarrowing) {
			machineMarrowingDisplay.draw(minecraft, machineMarrowingRegion.x(), machineMarrowingRegion.y());
		}
	}

	@Override
	public List<String> getTooltipStrings (final int mouseX, final int mouseY) {
		if (!requiresMachineMarrowing || !machineMarrowingRegionCell.isPosWithin(mouseX, mouseY))
			return Collections.emptyList();

		return Collections.singletonList(Translation.localize("jei.recipe.soulus:essence.requires_machine_marrowing_tooltip"));
	}
}
