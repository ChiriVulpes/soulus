package yuudaari.soulus.common.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.registration.Registration;

public class DustMidnight extends Registration.Item {

	public DustMidnight () {
		super("dust_midnight");
		setHasDescription();
	}

	@Override
	public EnumRarity getRarity (ItemStack stack) {
		return EnumRarity.RARE;
	}
}
