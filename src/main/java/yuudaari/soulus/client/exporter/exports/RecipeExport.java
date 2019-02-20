package yuudaari.soulus.client.exporter.exports;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IShapedRecipe;
import yuudaari.soulus.common.recipe.RecipeFurnace;
import yuudaari.soulus.common.recipe.composer.IRecipeComposer;
import yuudaari.soulus.common.util.serializer.CollectionSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class RecipeExport {

	@Serialized public final String name;
	@Serialized public final String type;
	@Serialized public final StackExport output;
	@Serialized(IngredientListSerializer.class) public final List<IngredientExport> ingredients;
	@Serialized public final float compositionTime;

	public RecipeExport (final IRecipe recipe) {
		this.name = recipe.getRegistryName().toString();
		this.type = getRecipeType(recipe);
		this.output = new StackExport(recipe.getRecipeOutput());
		this.compositionTime = recipe instanceof IRecipeComposer ? ((IRecipeComposer) recipe).getTime() : 1;
		this.ingredients = recipe.getIngredients()
			.stream()
			.map(ingredient -> new IngredientExport(ingredient))
			.collect(Collectors.toList());
	}

	private String getRecipeType (final IRecipe recipe) {
		if (recipe instanceof RecipeFurnace) {
			return "furnace";
		}

		if (recipe instanceof IRecipeComposer) {
			return recipe instanceof IShapedRecipe ? "composer_shaped" : "composer_shapeless";
		}

		return recipe instanceof IShapedRecipe ? "craft_shaped" : "craft_shapeless";
	}

	@Serializable
	public static class StackExport {

		@Serialized public final String item;
		@Serialized public final int data;
		@Serialized public final String nbt;
		@Serialized public final int count;

		public StackExport (final ItemStack stack) {
			this.item = stack.getItem().getRegistryName().toString();
			this.data = stack.getMetadata();
			this.nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";
			this.count = stack.getCount();
		}
	}

	@Serializable
	public static class IngredientExport {

		@Serialized(IngredientListSerializer.class) public final List<StackExport> matchingStacks;

		public IngredientExport (final Ingredient ingredient) {
			this.matchingStacks = Arrays.stream(ingredient.getMatchingStacks())
				.map(stack -> new StackExport(stack))
				.collect(Collectors.toList());
		}
	}

	public static class StackListSerializer extends CollectionSerializer<StackExport> {

		@Override
		public Class<StackExport> getValueClass () {
			return StackExport.class;
		}
	}

	public static class IngredientListSerializer extends CollectionSerializer<IngredientExport> {

		@Override
		public Class<IngredientExport> getValueClass () {
			return IngredientExport.class;
		}
	}
}
