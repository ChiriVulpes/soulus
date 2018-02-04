package yuudaari.soulus.common.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.item.BoneChunk;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.util.BoneType;

public class RecipeWrapperEssence implements IRecipeWrapper {

	private List<ItemStack> outputs;
	private ItemStack input;

	private ResourceLocation registryName;

	public ResourceLocation getRegistryName () {
		return registryName;
	}

	public RecipeWrapperEssence (BoneType boneType) {

		input = BoneChunk.boneChunkTypes.get(boneType).getItemStack();
		outputs = new ArrayList<>();

		outputs = Essence.CONFIG.essences.stream()
			.filter(e -> e.bones.type == boneType && !e.essence.equals("NONE"))
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
