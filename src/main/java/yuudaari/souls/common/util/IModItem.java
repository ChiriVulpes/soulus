package yuudaari.souls.common.util;

import yuudaari.souls.common.block.SoulsBlock;
import yuudaari.souls.common.block.SoulsBlockPane;
import yuudaari.souls.common.item.SoulsItem;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public interface IModItem extends IModObject {

	abstract void addOreDict(String name);

	abstract List<String> getOreDicts();

	default void registerRecipes() {
	}

	default ItemStack getItemStack() {
		if (this instanceof SoulsItem)
			return new ItemStack((SoulsItem) this);
		else if (this instanceof SoulsBlock)
			return new ItemStack(((SoulsBlock) this));
		else if (this instanceof SoulsBlockPane)
			return new ItemStack(((SoulsBlockPane) this));
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
		if (this instanceof SoulsItem)
			return (net.minecraft.item.Item) this;
		else if (this instanceof SoulsBlock)
			return (net.minecraft.item.Item) ((SoulsBlock) this).getItemBlock();
		else if (this instanceof SoulsBlockPane)
			return (net.minecraft.item.Item) ((SoulsBlockPane) this).getItemBlock();
		else
			throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}

	default SoulsItem castItem() {
		if (this instanceof SoulsItem)
			return (SoulsItem) this;
		else
			throw new IllegalArgumentException("Must be called on a valid Item");
	}

	default SoulsBlock castBlock() {
		if (this instanceof SoulsBlock)
			return (SoulsBlock) this;
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