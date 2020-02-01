package yuudaari.soulus.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigSledgehammer;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class Sledgehammer extends Registration.Item {

	@Inject public static ConfigSledgehammer CONFIG;

	public static enum Type {

		/**
		 * Order by tier
		 */
		NORMAL (null),
		ENDERSTEEL ("endersteel"),
		ENDERSTEEL_DARK ("endersteel_dark"),
		NIOBIUM ("niobium");

		public final String registryName;

		private Type (final String name) {
			registryName = "sledgehammer" + (name == null || name.length() == 0 ? "" : "_" + name);
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
}
