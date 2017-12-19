package yuudaari.soulus.common.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber
public class NoMobSpawning {
	/*
	@SubscribeEvent
	public static void catchPotentialSpawns(WorldEvent.PotentialSpawns event) {
		event.setCanceled(true);
	}
	*/

	@SubscribeEvent
	public static void onMobJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity == null || !(entity instanceof EntityLiving) || event.getWorld().isRemote
				|| Soulus.config.spawnChance == 1)
			return;

		NBTTagCompound entityData = entity.getEntityData();
		String entityName = entityData.getString("id");
		if (!Soulus.config.spawnEntityWhitelist.contains(entityName) || Soulus.config.spawnEntityBlacklist.size() > 0
				&& Soulus.config.spawnEntityBlacklist.contains(entityName)) {

			if (!entityData.hasKey("spawned_by_souls", 1) && (Soulus.config.spawnChance == 0
					|| event.getWorld().rand.nextDouble() >= Soulus.config.spawnChance)) {
				event.setCanceled(true);
			}
		}
	}
}
