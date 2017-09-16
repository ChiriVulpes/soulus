package yuudaari.souls.common.recipe.complex;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ingredient {

	// base ingredient
	public ItemStack stack;
	public Item item;
	public int data = 32767;
	public JsonObject searchObject;

	// 
	public Ingredient ing;
	public Matcher quantity;

	public boolean matches(ItemStack item) {
		return false;
	}

}