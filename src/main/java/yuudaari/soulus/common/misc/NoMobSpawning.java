package yuudaari.soulus.common.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.creature.ConfigCreature;
import yuudaari.soulus.common.config.creature.ConfigCreatureBiome;
import yuudaari.soulus.common.config.creature.ConfigCreatureDimension;
import yuudaari.soulus.common.config.creature.ConfigCreatures;
import yuudaari.soulus.common.config.misc.ConfigDespawn;
import yuudaari.soulus.common.config.misc.ConfigMobSpawnItems;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
@ConfigInjected(Soulus.MODID)
public class NoMobSpawning {

	@Inject public static ConfigCreatures CONFIG;
	@Inject public static ConfigMobSpawnItems CONFIG_SPAWN_ITEMS;
	@Inject public static ConfigDespawn CONFIG_DESPAWN;

	@SubscribeEvent
	public static void onMobJoinWorld (EntityJoinWorldEvent event) {

		// first we check if we should even try to cancel the spawn
		final Entity entity = event.getEntity();
		if (entity == null || !(entity instanceof EntityLiving) || event.getWorld().isRemote)
			return;

		final EntityLiving living = (EntityLiving) entity;

		// then we check if the creature has already been whitelisted
		if (SpawnType.get(living) != null)
			return;

		// we check if the creature was spawned by an egg or morb or whatever
		if (wasSpawnedFromItem(event)) {
			SpawnType.SPAWNED_FROM_EGG.apply(living);
			if (!CONFIG_DESPAWN.despawnMobsFromEggs)
				living.enablePersistence();
			return;
		}

		// we explicitly whitelist slimes that have persistence as it's likely they were from a summoned slime
		if (living.isNoDespawnRequired() && living instanceof EntitySlime) {
			SpawnType.SPAWNED.apply(living);
			return;
		}

		// then we get the dimension config for this potential spawn
		DimensionType dimension = event.getWorld().provider.getDimensionType();
		//Logger.info(dimension.getName());
		ConfigCreatureDimension dimensionConfig = CONFIG.dimensionConfigs.get(dimension.getName());
		if (dimensionConfig == null) {
			dimensionConfig = CONFIG.dimensionConfigs.get("*");
			if (dimensionConfig == null) {
				SpawnType.SPAWNED.apply(living);
				return;
			}
		}

		// then we get the biome config for this potential spawn
		BlockPos pos = living.getPosition();
		Biome biome = event.getWorld().getBiome(pos);
		//Logger.info(biome.getRegistryName().toString());
		ConfigCreatureBiome biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().toString());
		if (biomeConfig == null) {
			biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().getResourceDomain() + ":*");
			if (biomeConfig == null) {
				biomeConfig = dimensionConfig.biomeConfigs.get("*");
				if (biomeConfig == null) {
					SpawnType.SPAWNED.apply(living);
					return;
				}
			}
		}

		// then we get the creature config for this potential spawn
		String entityName = EntityList.getKey(living).toString();
		//Logger.info(entityName);
		ConfigCreature creatureConfig = biomeConfig.creatureConfigs.get(entityName);
		if (creatureConfig == null) {
			creatureConfig = biomeConfig.creatureConfigs
				.get(new ResourceLocation(entityName).getResourceDomain() + ":*");
			if (creatureConfig == null) {
				creatureConfig = biomeConfig.creatureConfigs.get("*");
				if (creatureConfig == null) {
					SpawnType.SPAWNED.apply(living);
					return;
				}
			}
		}

		// if we have 100% spawn chance, don't attempt to cancel
		if (creatureConfig.spawnChance == 1) {
			SpawnType.SPAWNED.apply(living);
			return;
		}

		// if we have 0% spawn chance, cancel, otherwise, randomly decide based on the spawn chance
		if (creatureConfig.spawnChance == 0 || event.getWorld().rand.nextDouble() >= creatureConfig.spawnChance) {
			event.setCanceled(true);
		}
	}

	////////////////////////////////////
	// The following code allows spawn eggs and other similar items from other mods to function
	// however it's super jank cause i couldn't find a better way to do it
	// it might break
	//

	// private static Tuple2<ItemStack, Long> savedRightClick;
	private static long lastAttemptedSpawn = 0;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRightClick (final RightClickBlock event) {
		lastAttemptedSpawn = 0;

		if (event.isCanceled() || event.getUseItem() == Result.DENY)
			return;

		final ItemStack stack = event.getItemStack();

		if (CONFIG_SPAWN_ITEMS.isSpawningItem(stack))
			// savedRightClick = new Tuple2<>(stack, System.currentTimeMillis());
			lastAttemptedSpawn = System.currentTimeMillis();
	}

	@SubscribeEvent
	public static void canEntitySpawn (final LivingSpawnEvent.CheckSpawn event) {
		lastAttemptedSpawn = 0;
	}

	private static boolean wasSpawnedFromItem (final EntityJoinWorldEvent event) {
		// if (lastAttemptedSpawn == 0)
		// 	return false;

		// final Tuple2<ItemStack, Long> saved = savedRightClick;
		// savedRightClick = null;

		// final long rightClickTime = saved._2();
		if (System.currentTimeMillis() - lastAttemptedSpawn > 1)
			return false;

		// final ItemStack stack = saved._1();

		// Logger.info("allowing spawn of: " + event.getEntity().getName() + ", from item: " + stack.getDisplayName());

		return true;
	}
}
