package yuudaari.soulus.common.recipe.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.Flags;

@ConfigInjected(Soulus.MODID)
public class IngredientEssence extends Ingredient {

	@Inject public static ConfigEssences CONFIG;

	public static enum AllowedStack {

		ASH (ItemRegistry.ASH.getItemStack()),
		EMPTY (ItemStack.EMPTY),
		ESSENCE_PERFECT (ItemRegistry.ESSENCE_PERFECT.getPerfectStack());

		public final ItemStack stack;

		private AllowedStack (final ItemStack stack) {
			this.stack = stack;
		}
	}

	private static Map<Integer, IngredientEssence> INSTANCES = new HashMap<>();

	public static IngredientEssence getInstance (final AllowedStack... allowedStacks) {
		final Flags<AllowedStack> flags = Flags.get(allowedStacks);
		final int id = flags.toInt();
		return INSTANCES.computeIfAbsent(id, __ -> new IngredientEssence(flags));
	}

	public static ItemStack[] initMatchingStacks (Flags<AllowedStack> flags) {
		List<ItemStack> stacks = new ArrayList<>();

		flags.map(stack -> stack.stack)
			.forEach(stacks::add);

		CONFIG.getEssences()
			.map(config -> config.essence)
			.map(Essence::getStack)
			.forEach(stacks::add);

		return stacks.toArray(new ItemStack[0]);
	}

	private final Flags<AllowedStack> flags;

	public IngredientEssence () {
		this(Flags.get(AllowedStack.EMPTY, AllowedStack.ASH));
	}

	public IngredientEssence (final Flags<AllowedStack> flags) {
		super(initMatchingStacks(flags));
		this.flags = flags;
	}

	@Override
	public boolean apply (ItemStack input) {
		return (input.getItem() == ItemRegistry.ESSENCE || //
			(flags.has(AllowedStack.EMPTY) && (input == null || input.isEmpty()))) || //
			(flags.has(AllowedStack.ASH) && input.getItem() == ItemRegistry.ASH) || //
			(flags.has(AllowedStack.ESSENCE_PERFECT) && input.getItem() == ItemRegistry.ESSENCE_PERFECT);
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			return getInstance(Arrays.stream(AllowedStack.values())
				.filter(allowedStack -> JsonUtils.getBoolean(json, "allow_" + allowedStack.name().toLowerCase(), false))
				.toArray(AllowedStack[]::new));
		}
	}
}
