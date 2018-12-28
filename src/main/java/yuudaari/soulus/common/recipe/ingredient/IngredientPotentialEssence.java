package yuudaari.soulus.common.recipe.ingredient;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.item.Essence;

@ConfigInjected(Soulus.MODID)
public class IngredientPotentialEssence extends Ingredient {

	@Inject public static ConfigEssences CONFIG;

	private static IngredientPotentialEssence INSTANCE = null;
	private static IngredientPotentialEssence INSTANCE_NO_ASH = null;

	public static IngredientPotentialEssence getInstance () {
		if (INSTANCE == null) INSTANCE = new IngredientPotentialEssence();
		return INSTANCE;
	}

	public static IngredientPotentialEssence getInstanceNoAsh () {
		if (INSTANCE_NO_ASH == null) INSTANCE_NO_ASH = new IngredientPotentialEssence(false, true);
		return INSTANCE_NO_ASH;
	}

	public static ItemStack[] getMatchingStacks1 (boolean justEssence) {
		List<ItemStack> stacks = new ArrayList<>();

		if (!justEssence) {
			stacks.add(ItemStack.EMPTY);
			stacks.add(ModItems.ASH.getItemStack());
		}

		for (ConfigEssence essenceConfig : CONFIG.essences) {
			if (essenceConfig.essence.equals("NONE"))
				continue;
			stacks.add(Essence.getStack(essenceConfig.essence));
		}

		return stacks.toArray(new ItemStack[0]);
	}

	private final boolean allowEmpty;
	private final boolean allowAsh;

	public IngredientPotentialEssence () {
		this(true, true);
	}

	public IngredientPotentialEssence (boolean allowAsh, boolean allowEmpty) {
		super(getMatchingStacks1(!allowAsh));
		this.allowAsh = allowAsh;
		this.allowEmpty = allowEmpty;
	}

	@Override
	public boolean apply (ItemStack input) {
		return (allowEmpty && (input == null || input.isEmpty())) || //
			input.getItem() == ModItems.ESSENCE || // 
			(allowAsh && input.getItem() == ModItems.ASH);
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			final boolean allowAsh = json.has("allow_ash") ? JsonUtils.getBoolean(json, "allow_ash") : true;
			final boolean allowEmpty = json.has("allow_empty") ? JsonUtils.getBoolean(json, "allow_ash") : true;
			return new IngredientPotentialEssence(allowAsh, allowEmpty);
		}
	}
}
