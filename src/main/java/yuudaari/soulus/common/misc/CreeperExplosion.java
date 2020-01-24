package yuudaari.soulus.common.misc;

import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public class CreeperExplosion {

	@SubscribeEvent
	public static void onExplode (ExplosionEvent event) {
		Entity exploder = event.getExplosion().exploder;
		if (!(exploder instanceof EntityCreeper)) return;

		EntityCreeper creeper = (EntityCreeper) exploder;

		for (PotionEffect potionEffect : creeper.getActivePotionEffects().stream().collect(Collectors.toList())) {
			if (potionEffect.getDuration() > Short.MAX_VALUE) {
				creeper.removePotionEffect(potionEffect.getPotion());
			}
		}
	}
}
