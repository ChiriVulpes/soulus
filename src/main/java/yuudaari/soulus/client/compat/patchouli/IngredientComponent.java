package yuudaari.soulus.client.compat.patchouli;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

public class IngredientComponent implements ICustomComponent {

	public static String serialize (Ingredient ingredient) {
		final ItemStack[] stacks = ingredient.getMatchingStacks();
		final String[] stacksSerialized = new String[stacks.length];
		for (int i = 0; i < stacks.length; i++) {
			stacksSerialized[i] = PatchouliAPI.instance.serializeItemStack(stacks[i]);
		}

		return String.join(",", stacksSerialized);
	}

	public static Ingredient deserialize (String ingredientSerialized) {
		final String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientSerialized);
		final ItemStack[] stacks = new ItemStack[stacksSerialized.length];
		for (int i = 0; i < stacksSerialized.length; i++) {
			stacks[i] = PatchouliAPI.instance.deserializeItemStack(stacksSerialized[i]);
		}

		return Ingredient.fromStacks(stacks);
	}

	private static String[] splitStacksFromSerializedIngredient (String ingredientSerialized) {
		final List<String> result = new ArrayList<>();

		int lastIndex = 0;
		int braces = 0;
		boolean insideString = false;
		for (int i = 0; i < ingredientSerialized.length(); i++) {
			switch (ingredientSerialized.charAt(i)) {
				case '{': {
					if (!insideString) braces++;
					break;
				}
				case '}': {
					if (!insideString) braces--;
					break;
				}
				case '\'': {
					insideString = !insideString;
					break;
				}
				case ',': {
					if (braces <= 0) {
						result.add(ingredientSerialized.substring(lastIndex, i));
						lastIndex = i + 1;
						break;
					}
				}
			}
		}

		result.add(ingredientSerialized.substring(lastIndex));

		return result.toArray(new String[result.size()]);
	}

	@SerializedName("ingredient") @VariableHolder public String ingredientSerialized;

	private transient int x, y;
	private transient GuiBook parent;
	private transient Ingredient ingredient;

	@Override
	public void build (int x, int y, int page) {
		this.x = x;
		this.y = y;

		this.ingredient = IngredientComponent.deserialize(ingredientSerialized);
	}

	@Override
	public void onDisplayed (GuiScreen parent) {
		this.parent = (GuiBook) parent;
	}

	@Override
	public void render (float ticks, int mouseX, int mouseY) {
		renderIngredient(x, y, mouseX, mouseY, ingredient);
	}

	private void renderIngredient (int x, int y, int mouseX, int mouseY, Ingredient ingr) {
		ItemStack[] stacks = ingr.getMatchingStacks();
		if (stacks.length > 0)
			renderItem(x, y, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length]);
	}

	private void renderItem (int x, int y, int mouseX, int mouseY, ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		Minecraft.getMinecraft().getRenderItem().renderItemOverlays(parent.fontRenderer, stack, x, y);

		if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16))
			parent.setTooltipStack(stack);
	}
}
