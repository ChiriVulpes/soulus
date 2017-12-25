package yuudaari.soulus.common.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.recipe.Recipe;

public interface IModItem {
	abstract String getName();

	default void setName(String name) {
	}

	default IModItem addOreDict(String... name) {
		return this;
	}

	default List<String> getOreDicts() {
		return new ArrayList<>();
	}

	default List<Recipe> getRecipes() {
		return new ArrayList<>();
	}

	default void addRecipe(Recipe recipe) {
	}

	default ItemStack getItemStack() {
		if (this instanceof Item)
			return new ItemStack((Item) this);
		else if (this instanceof Block)
			return new ItemStack(((Block) this));
		else
			throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}

	default ItemStack getItemStack(Integer count) {
		ItemStack result = getItemStack();
		result.setCount(count);
		return result;
	}

	default ItemStack getItemStack(Integer count, Integer meta) {
		ItemStack result = getItemStack();
		result.setCount(count);
		result.setItemDamage(meta);
		return result;
	}

	@SideOnly(Side.CLIENT)
	default void registerModels() {
		ModelLoader.setCustomModelResourceLocation((Item) this, 0,
				new ModelResourceLocation(((Item) this).getRegistryName(), "inventory"));
	}
}