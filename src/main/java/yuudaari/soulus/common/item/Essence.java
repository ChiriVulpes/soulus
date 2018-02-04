package yuudaari.soulus.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigColor;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.Soulus;

@ConfigInjected(Soulus.MODID)
public class Essence extends ModItem {

	@Inject public static ConfigEssences CONFIG;

	public static ItemStack getStack (String essenceType) {
		return getStack(essenceType, 1);
	}

	public static ItemStack getStack (String essenceType, Integer count) {
		ItemStack stack = new ItemStack(ModItems.ESSENCE, count);
		EssenceType.setEssenceType(stack, essenceType);
		return stack;
	}

	public Essence () {
		super("essence");
		setMaxStackSize(64);
		setCreativeTab(null);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (ItemStack stack, int tintIndex) -> {
				String essenceType = EssenceType.getEssenceType(stack);
				if (essenceType == null)
					return -1;

				EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(essenceType));
				if (entry == null)
					return -1;

				ConfigEssence essenceConfig = CONFIG.get(essenceType);
				if (essenceConfig == null)
					return -1;

				ConfigColor colors = essenceConfig.colors;
				if (colors == null) {
					EntityList.EntityEggInfo eggInfo = entry.getEgg();
					if (eggInfo == null)
						return -1;
					colors = new ConfigColor(eggInfo);
				}

				return tintIndex == 0 ? colors.primary : colors.secondary;
			});
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		ConfigEssence config = CONFIG.get(essenceType);
		if (essenceType == null || config == null)
			return I18n.format(this.getUnlocalizedName() + ".unfocused.name").trim();

		String alignment = config.name;
		if (alignment == null) {
			String translationKey = "entity." + essenceType + ".name";
			alignment = I18n.format(translationKey);
			if (translationKey.equals(alignment)) {
				alignment = I18n
					.format("entity." + EntityList.getTranslationName(new ResourceLocation(essenceType)) + ".name");
			}
		}

		return I18n.format(this.getUnlocalizedName() + ".focused.name", alignment).trim();
	}

	@Override
	public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		for (ConfigEssence essence : CONFIG.essences) {
			if (essence.essence.equals("NONE")) continue;

			items.add(getStack(essence.essence));
		}
	}

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		registry.add(new IngredientPotentialEssence(true), getRegistryName());
	}
}
