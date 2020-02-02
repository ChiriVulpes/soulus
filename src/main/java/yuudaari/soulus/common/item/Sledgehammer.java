package yuudaari.soulus.common.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigSledgehammer;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class Sledgehammer extends Registration.Item {

	@Inject public static ConfigSledgehammer CONFIG;

	public static String ORE_DICT = "sledgehammerSoulus";

	public static enum Type {

		/**
		 * Order by tier
		 */
		NORMAL (null, EnumRarity.COMMON),
		ENDERSTEEL ("endersteel", EnumRarity.UNCOMMON),
		ENDERSTEEL_DARK ("endersteel_dark", EnumRarity.RARE),
		NIOBIUM ("niobium", EnumRarity.EPIC);

		public final String registryName;
		public final EnumRarity rarity;

		private Type (final String name, final EnumRarity rarity) {
			registryName = "sledgehammer" + (name == null || name.length() == 0 ? "" : "_" + name);
			this.rarity = rarity;
		}

		public boolean isMaxTier () {
			return ordinal() >= values().length - 1;
		}

	}

	public final Type type;

	public Sledgehammer (final Type type) {
		super(type.registryName);
		this.type = type;
		setMaxStackSize(1);
		setMaxDamage(256);
		setHasDescription();
		final ItemStack stack = getItemStack();
		stack.setItemDamage(OreDictionary.WILDCARD_VALUE);
		addOreDict(stack, ORE_DICT);
	}

	@Override
	public int getMaxDamage (ItemStack stack) {
		switch (type) {
			case ENDERSTEEL:
				return CONFIG.durabilityEndersteel;
			case ENDERSTEEL_DARK:
				return CONFIG.durabilityEndersteelDark;
			case NIOBIUM:
				return CONFIG.durabilityNiobium;
			default:
				return CONFIG.durability;
		}
	}

	@Override
	public EnumRarity getRarity (ItemStack stack) {
		return type.rarity;
	}
}
