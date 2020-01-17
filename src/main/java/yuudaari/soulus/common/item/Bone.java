package yuudaari.soulus.common.item;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.Registration;

public class Bone extends Registration.Item implements IBone {

	private boolean isWolfsFavouriteBone = false;

	public Bone (final String name) {
		super(name);
		addOreDict("bone");
		setHasDescription();
	}

	public Bone setIsWolfsFavouriteBone () {
		isWolfsFavouriteBone = true;
		return this;
	}

	@Override
	public String getDescriptionRegistryName () {
		return Soulus.MODID + ":bone";
	}

	@Override
	public double feedToWolf (final EntityWolf wolf, final ItemStack stack, final EntityPlayer player) {
		return 1.0 / (isWolfsFavouriteBone ? 3 : 12);
	}
}
