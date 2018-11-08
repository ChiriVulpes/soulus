package yuudaari.soulus.common.misc;

import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CreeperExplosion {

	@SubscribeEvent
	public static void onExplode (ExplosionEvent event) {
		EntityLivingBase exploder = (EntityLivingBase) event.getExplosion().exploder;

		for (PotionEffect potionEffect : exploder.getActivePotionEffects().stream().collect(Collectors.toList())) {
			if (potionEffect.getDuration() > Short.MAX_VALUE) {
				exploder.removePotionEffect(potionEffect.getPotion());
			}
		}
	}
}
