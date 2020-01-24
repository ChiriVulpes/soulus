package yuudaari.soulus.common.misc;

import java.util.Random;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.misc.ConfigBreeding;

@ConfigInjected(Soulus.MODID)
@Mod.EventBusSubscriber(modid = Soulus.MODID)
public class MobBreeding {

	@Inject public static ConfigBreeding CONFIG;

	@SubscribeEvent
	public static void babyEntitySpawn (BabyEntitySpawnEvent event) {
		EntityAgeable entity = event.getChild();
		String entityName = EntityList.getKey(entity).toString();
		double chance = CONFIG.getChance(entityName);

		if (new Random().nextDouble() > chance) {
			event.setCanceled(true);

		} else {
			NoMobSpawning.approveSpawn(entity);
		}
	}
}
