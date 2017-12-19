package yuudaari.soulus.common.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.common.config.Config;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.config.Serializer;

@Mod.EventBusSubscriber
public class NoMobSpawning {
	public static NoMobSpawning INSTANCE = new NoMobSpawning();

	public static Serializer<NoMobSpawning> serializer = new Serializer<>(NoMobSpawning.class, "spawnChance");
	static {
		serializer.fieldHandlers.put("entityWhitelist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));
		serializer.fieldHandlers.put("entityBlacklist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));

		serializer.fieldHandlers.put("biomeWhitelist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));
		serializer.fieldHandlers.put("biomeBlacklist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));

		serializer.fieldHandlers.put("dimensionWhitelist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));
		serializer.fieldHandlers.put("dimensionBlacklist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));
	}

	public double spawnChance = 0;
	public List<String> entityWhitelist = new ArrayList<>();
	public List<String> entityBlacklist = new ArrayList<>();
	public List<String> biomeWhitelist = new ArrayList<>();
	public List<String> biomeBlacklist = new ArrayList<>();
	public List<String> dimensionWhitelist = new ArrayList<>();
	public List<String> dimensionBlacklist = new ArrayList<>();

	@SubscribeEvent
	public static void onMobJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity == null || !(entity instanceof EntityLiving) || event.getWorld().isRemote)
			return;

		NBTTagCompound entityData = entity.getEntityData();
		String entityName = entityData.getString("id");
		if (!INSTANCE.entityWhitelist.contains(entityName)
				|| INSTANCE.entityBlacklist.size() > 0 && INSTANCE.entityBlacklist.contains(entityName)) {

			if (!entityData.hasKey("spawned_by_souls", 1) && INSTANCE.spawnChance == 0
					|| event.getWorld().rand.nextDouble() < INSTANCE.spawnChance) {
				event.setCanceled(true);
			}
		}
	}
}
