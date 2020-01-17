package yuudaari.soulus.common.item;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.registration.Registration;

public class BoneScale extends Registration.Item implements IBone {

	public BoneScale () {
		super("bone_scale");
		setHasDescription();
	}

	@Override
	public double feedToWolf (final EntityWolf wolf, final ItemStack stack, final EntityPlayer player) {
		return 1.0 / 12.0;
	}
}
