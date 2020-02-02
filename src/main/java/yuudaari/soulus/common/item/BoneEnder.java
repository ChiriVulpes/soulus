package yuudaari.soulus.common.item;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class BoneEnder extends Registration.Item implements IBone {

	public BoneEnder () {
		super("bone_ender");
		setHasGlint();
		setHasDescription();
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void feedToWolf (final EntityWolf wolf, final ItemStack stack, final EntityPlayer player) {
		wolf.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 500));
	}
}
