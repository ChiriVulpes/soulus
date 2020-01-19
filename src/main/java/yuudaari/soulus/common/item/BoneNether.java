package yuudaari.soulus.common.item;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import yuudaari.soulus.common.registration.Registration;

public class BoneNether extends Registration.Item implements IBone {

	public BoneNether () {
		super("bone_nether");
		setHasDescription();
	}

	@Override
	public void feedToWolf (final EntityWolf wolf, final ItemStack stack, final EntityPlayer player) {
		wolf.setAttackTarget(player);
		if (player.world.rand.nextBoolean())
			wolf.addPotionEffect(new PotionEffect(MobEffects.WITHER, 1000));
	}
}
