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

	public Sledgehammer () {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(256);
		setHasDescription();
	}

	@Override
	public int getMaxDamage (ItemStack stack) {
		return CONFIG.durability;
	}
}
