package yuudaari.soulus.common.recipe;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.recipe.composer.IRecipeComposer;

@Mod.EventBusSubscriber
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

	/**
	 * Remove invalid composer recipes after they're registered
	 */
	@SubscribeEvent
	public static void registerRecipes (final RegistryEvent.Register<IRecipe> event) {
		final IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();

		final List<ResourceLocation> toRemove = new ArrayList<>();
		for (final IRecipe recipe : registry) {
			final boolean isSoulusRecipe = recipe instanceof IRecipeComposer || //
				recipe instanceof RecipeShaped || //
				recipe instanceof RecipeShapeless || //
				recipe.getRegistryName().getResourceDomain().equalsIgnoreCase(Soulus.MODID);

			if (isSoulusRecipe && recipe.getRecipeOutput().isEmpty())
				toRemove.add(recipe.getRegistryName());
		}

		for (final ResourceLocation name : toRemove) {
			registry.remove(name);
		}
	}
}
