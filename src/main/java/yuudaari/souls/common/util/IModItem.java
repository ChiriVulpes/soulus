package yuudaari.souls.common.util;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yuudaari.souls.common.recipe.Recipe;

public interface IModItem {
	abstract String getName();

	abstract void setName(String name);

	abstract IModItem addOreDict(String... name);

	abstract List<String> getOreDicts();

	abstract List<Recipe> getRecipes();

	abstract void addRecipe(Recipe recipe);

	default ItemStack getItemStack() {
		if (this instanceof ModItem)
			return new ItemStack((ModItem) this);
		else if (this instanceof ModBlock)
			return new ItemStack(((ModBlock) this));
		else if (this instanceof ModBlockPane)
			return new ItemStack(((ModBlockPane) this));
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

	default net.minecraft.item.Item getItem() {
		if (this instanceof ModItem)
			return (net.minecraft.item.Item) this;
		else if (this instanceof ModBlock)
			return (net.minecraft.item.Item) ((ModBlock) this).getItemBlock();
		else if (this instanceof ModBlockPane)
			return (net.minecraft.item.Item) ((ModBlockPane) this).getItemBlock();
		else
			throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}

	default ModItem castItem() {
		if (this instanceof ModItem)
			return (ModItem) this;
		else
			throw new IllegalArgumentException("Must be called on a valid Item");
	}

	default ModBlock castBlock() {
		if (this instanceof ModBlock)
			return (ModBlock) this;
		else
			throw new IllegalArgumentException("Must be called on a valid Block");
	}

	default void addFurnaceRecipe(Item result) {
		GameRegistry.addSmelting(this.getItem(), new ItemStack(result), 0.35F);
	}

	default void addFurnaceRecipe(Item result, float xp) {
		GameRegistry.addSmelting(this.getItem(), new ItemStack(result), xp);
	}

	default void addFurnaceRecipe(ItemStack result) {
		GameRegistry.addSmelting(this.getItem(), result, 0.35F);
	}

	default void addFurnaceRecipe(ItemStack result, float xp) {
		GameRegistry.addSmelting(this.getItem(), result, xp);
	}

	default void addFurnaceRecipe(int count, ItemStack result, float xp) {
		GameRegistry.addSmelting(this.getItemStack(count), result, xp);
	}

	default void addFurnaceRecipeAsOutput(Item input) {
		GameRegistry.addSmelting(input, this.getItemStack(), 0.35F);
	}

	default void addFurnaceRecipeAsOutput(Item input, float xp) {
		GameRegistry.addSmelting(input, this.getItemStack(), xp);
	}

	default void addFurnaceRecipeAsOutput(Item input, int count) {
		GameRegistry.addSmelting(input, this.getItemStack(count), 0.35F);
	}

	default void addFurnaceRecipeAsOutput(Item input, int count, float xp) {
		GameRegistry.addSmelting(input, this.getItemStack(count), xp);
	}
}