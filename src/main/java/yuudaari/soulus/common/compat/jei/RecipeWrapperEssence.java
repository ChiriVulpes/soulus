package yuudaari.soulus.common.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.item.Essence;

public class RecipeWrapperEssence implements IRecipeWrapper {

	private List<ItemStack> outputs;
	private ItemStack input;

	private ResourceLocation registryName;

	public ResourceLocation getRegistryName () {
		return registryName;
	}

	public RecipeWrapperEssence (ConfigBoneType boneType) {

		input = boneType.getChunkStack();
		outputs = new ArrayList<>();

		outputs = Essence.CONFIG.essences.stream()
			.filter(e -> e.bones != null && e.bones.type.equalsIgnoreCase(boneType.name) && !e.essence.equals("NONE"))
			.sorted( (e1, e2) -> (int) Math.signum(e2.bones.dropWeight - e1.bones.dropWeight))
			.map(e -> Essence.getStack(e.essence))
			.collect(Collectors.toList());
	}

	@Override
	public void getIngredients (IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, outputs);
	}
}
