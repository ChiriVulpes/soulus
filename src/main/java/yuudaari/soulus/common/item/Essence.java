package yuudaari.soulus.common.item;

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
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.compat.jei.JeiDescriptionRegistry;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigColor;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class Essence extends Registration.Item {

	@Inject public static ConfigEssences CONFIG;

	public static ItemStack getStack (String essenceType) {
		return getStack(essenceType, 1);
	}

	public static ItemStack getStack (String essenceType, Integer count) {
		ItemStack stack = new ItemStack(ItemRegistry.ESSENCE, count);
		EssenceType.setEssenceType(stack, essenceType);
		return stack;
	}

	public static int getColor (final String essenceType, final int index) {
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

		return index == 0 ? colors.primary : colors.secondary;
	}

	public Essence () {
		super("essence");
		setMaxStackSize(64);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (ItemStack stack, int tintIndex) -> {
				String essenceType = EssenceType.getEssenceType(stack);
				return getColor(essenceType, tintIndex);
			});
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		ConfigEssence config = CONFIG.get(essenceType);
		if (essenceType == null || config == null)
			return Translation.localize(this.getUnlocalizedName() + ".unfocused.name").trim();

		String alignment = config.name;
		if (alignment == null)
			alignment = EssenceType.localize(essenceType);

		return Translation.localize(this.getUnlocalizedName() + ".focused.name", alignment).trim();
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
		registry.add(new IngredientPotentialEssence(false, false), getRegistryName());
	}
}
