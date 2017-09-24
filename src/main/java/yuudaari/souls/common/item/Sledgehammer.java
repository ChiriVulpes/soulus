package yuudaari.souls.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.souls.common.config.ManualSerializer;
import yuudaari.souls.common.util.Logger;
import yuudaari.souls.common.util.ModItem;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Random;

public class Sledgehammer extends ModItem {

	private int defaultDurability = 256;

	public static ManualSerializer serializer = new ManualSerializer(Sledgehammer::serialize,
			Sledgehammer::deserialize);

	public Sledgehammer() {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(defaultDurability);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	private final Random random = new Random();

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		stack.attemptDamageItem(1, random, null);
		ItemStack newStack = new ItemStack(stack.getItem());
		newStack.setItemDamage(stack.getItemDamage());
		return newStack;
	}

	@SuppressWarnings("deprecation")
	private static JsonElement serialize(Object from) {
		JsonObject result = new JsonObject();

		result.add("durability", new JsonPrimitive(((Sledgehammer) from).getMaxDamage()));

		return result;
	}

	private static Object deserialize(JsonElement from, Object current) {

		if (from == null || !from.isJsonObject()) {
			Logger.warn("Must be an object");
			return current;
		}

		JsonElement durability = from.getAsJsonObject().get("durability");

		if (durability == null || !durability.isJsonPrimitive() || !durability.getAsJsonPrimitive().isNumber()) {
			Logger.warn("Property 'durability' must be an int");
			return current;
		}

		((Sledgehammer) current).setMaxDamage(durability.getAsInt());

		return current;
	}
}
