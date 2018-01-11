package yuudaari.soulus.common.item;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.config.ColorConfig;
import yuudaari.soulus.common.config.EssenceConfig;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.Soulus;

public class Essence extends ModItem {

	public static Essence INSTANCE = new Essence();

	public static ItemStack getStack(String essenceType) {
		return getStack(essenceType, 1);
	}

	public static ItemStack getStack(String essenceType, Integer count) {
		ItemStack stack = new ItemStack(INSTANCE, count);
		EssenceType.setEssenceType(stack, essenceType);
		return stack;
	}

	public Essence() {
		super("essence");
		setMaxStackSize(64);
		setCreativeTab(null);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler((ItemStack stack, int tintIndex) -> {
				String essenceType = EssenceType.getEssenceType(stack);
				if (essenceType == null)
					return -1;

				EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(essenceType));
				if (entry == null)
					return -1;

				EntityList.EntityEggInfo eggInfo = entry.getEgg();
				if (eggInfo == null)
					return -1;
				ColorConfig colors = new ColorConfig(eggInfo);

				EssenceConfig essenceConfig = Soulus.config.essences.get(essenceType);
				if (essenceConfig.colors.wasSet)
					colors = essenceConfig.colors;

				return tintIndex == 0 ? colors.primary : colors.secondary;
			});
		}
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String mobTarget = EssenceType.getEssenceType(stack);
		if (mobTarget == null)
			mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + mobTarget;
	}

	@Override
	public void onRegisterDescription(JeiDescriptionRegistry registry) {
		registry.add(new IngredientPotentialEssence(true), getRegistryName());
	}
}