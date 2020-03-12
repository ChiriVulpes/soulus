package yuudaari.soulus.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.oredict.OreDictionary;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public class RecipeUtils {

	public static ItemStack getOutput (JsonElement resultProperty, JsonContext context) {
		if (resultProperty.isJsonObject()) {
			return getOreDictItem(resultProperty.getAsJsonObject(), context);

		} else if (resultProperty.isJsonArray()) {
			for (JsonElement entry : resultProperty.getAsJsonArray()) {
				if (!resultProperty.isJsonObject()) continue;
				try {
					return getOutput(entry, context);
				} catch (JsonSyntaxException e) {
				}
			}
		}

		throw new JsonSyntaxException("Invalid recipe output.");
	}

	private static ItemStack getOreDictItem (JsonObject result, JsonContext context) {
		if (JsonUtils.getString(result, "type", "").equals("forge:ore_dict")) {
			String ore = JsonUtils.getString(result, "ore");
			ItemStack stack = OreDictionary.getOres(ore).stream().findAny().orElse(ItemStack.EMPTY).copy();
			stack.setCount(JsonUtils.getInt(result, "count", 1));
			int data = JsonUtils.getInt(result, "data", -1);
			if (data > -1) stack.setItemDamage(data);
			return stack;

		} else {
			return CraftingHelper.getItemStack(result, context);
		}
	}

}
