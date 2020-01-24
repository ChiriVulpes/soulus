package yuudaari.soulus.common.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.creature.ConfigCreature;
import yuudaari.soulus.common.config.creature.ConfigCreatureBiome;
import yuudaari.soulus.common.config.creature.ConfigCreatureDimension;
import yuudaari.soulus.common.config.creature.ConfigCreatureDrops;
import yuudaari.soulus.common.config.creature.ConfigCreatures;
import yuudaari.soulus.common.config.essence.ConfigCreatureLoot;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.util.Range;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
@ConfigInjected(Soulus.MODID)
public class BoneDrops {

	@Inject public static ConfigCreatures CONFIG_CREATURES;
	@Inject public static ConfigEssences CONFIG_ESSENCES;
	@Inject public static ConfigBoneTypes CONFIG_BONE_TYPES;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onMobDrops (LivingDropsEvent event) {

		// first we check if we should even try to do drops
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null || !(entity instanceof EntityLiving))
			return;

		List<EntityItem> drops = event.getDrops();

		String entityName = EntityList.getKey(entity).toString();
		ConfigCreature config = getCreatureConfig(entity, entityName);

		if (config != null) {
			// Logger.info("added bone drops");
			BoneDrops.addBoneDrops(entity, entityName, drops);

			boolean wasSummoned = entity.getEntityData().getByte("soulus:spawn_whitelisted") == (byte) 2;
			// we explicitly whitelist slimes that have persistence as it's likely they were from a summoned slime
			if (!wasSummoned && ((EntityLiving) entity).isNoDespawnRequired() && entity instanceof EntitySlime)
				wasSummoned = true;

			String spawnType = wasSummoned ? "summoned" : "spawned";

			ConfigCreatureDrops dropConfig = config.drops.get(spawnType);
			ConfigCreatureDrops dropConfigAll = config.drops.get("all");

			if (dropConfig == null && dropConfigAll == null) {
				return;

			} else {
				List<String> emptyList = new ArrayList<>();
				boolean dc = dropConfig != null;
				boolean dca = dropConfigAll != null;
				List<String> whitelistedDrops = dc ? dropConfig.whitelistedDrops : emptyList;
				List<String> blacklistedDrops = dc ? dropConfig.blacklistedDrops : emptyList;
				List<String> whitelistedDropsAll = dca ? dropConfigAll.whitelistedDrops : emptyList;
				List<String> blacklistedDropsAll = dca ? dropConfigAll.blacklistedDrops : emptyList;
				boolean allBlacklisted = blacklistedDrops.contains("*");
				if (allBlacklisted) {
					// Logger.info("all cleared all blacklisted");
					drops.clear();
					return;
				}

				if (!(whitelistedDrops.contains("*") || whitelistedDropsAll.contains("*")) || blacklistedDrops
					.size() + blacklistedDropsAll.size() > 0) {
					drops.removeIf(existingDrop -> {
						ResourceLocation res = existingDrop.getItem().getItem().getRegistryName();
						String modWild = res.getResourceDomain() + ":*";
						String name = res.toString();
						if (!whitelistedDrops.contains("*") && !whitelistedDropsAll.contains("*")) {
							if (!whitelistedDrops.contains(modWild) && !whitelistedDropsAll.contains(modWild)) {
								if (!whitelistedDrops.contains(name) && !whitelistedDropsAll.contains(name)) {
									return true;
								}
							}
						}

						if (allBlacklisted || blacklistedDrops.contains(modWild) || blacklistedDrops
							.contains(name) || (blacklistedDropsAll.contains(modWild) && !(whitelistedDrops
								.contains(modWild) || whitelistedDrops.contains(name))) || (blacklistedDropsAll
									.contains(name) && !(whitelistedDrops.contains(modWild) || whitelistedDrops
										.contains(name)))) {
							return true;
						}

						return false;
					});
				}
			}
		}
	}

	private static void addBoneDrops (EntityLivingBase entity, String entityName, List<EntityItem> drops) {
		for (ConfigEssence essenceConfig : CONFIG_ESSENCES.essences) {
			if (essenceConfig.loot == null) continue;

			for (Map.Entry<String, ConfigCreatureLoot> lootConfig : essenceConfig.loot.entrySet()) {
				if (essenceConfig.bones != null && lootConfig.getKey().equals(entityName)) {
					ItemStack stack = getStack(entity.world.rand, essenceConfig.bones.type, lootConfig.getValue());
					if (stack != null) {
						drops.add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack));
					}
					break;
				}
			}
		}
	}

	private static ConfigCreature getCreatureConfig (EntityLivingBase entity) {
		return getCreatureConfig(entity, EntityList.getKey(entity).toString());
	}

	private static ConfigCreature getCreatureConfig (EntityLivingBase entity, String entityName) {
		// then we get the dimension config for this potential spawn
		DimensionType dimension = entity.world.provider.getDimensionType();
		// Logger.info(dimension.getName());
		ConfigCreatureDimension dimensionConfig = CONFIG_CREATURES.dimensionConfigs.get(dimension.getName());
		if (dimensionConfig == null) {
			dimensionConfig = CONFIG_CREATURES.dimensionConfigs.get("*");
			if (dimensionConfig == null) {
				return null;
			}
		}

		// then we get the biome config for this potential spawn
		BlockPos pos = entity.getPosition();
		Biome biome = entity.world.getBiome(pos);
		// Logger.info(biome.getRegistryName().toString());
		ConfigCreatureBiome biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().toString());
		if (biomeConfig == null) {
			biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().getResourceDomain() + ":*");
			if (biomeConfig == null) {
				biomeConfig = dimensionConfig.biomeConfigs.get("*");
				if (biomeConfig == null) {
					return null;
				}
			}
		}

		// then we get the creature config for this potential spawn
		// Logger.info(entityName);
		ConfigCreature creatureConfig = biomeConfig.creatureConfigs.get(entityName);
		if (creatureConfig == null) {
			creatureConfig = biomeConfig.creatureConfigs
				.get(new ResourceLocation(entityName).getResourceDomain() + ":*");
			if (creatureConfig == null) {
				creatureConfig = biomeConfig.creatureConfigs.get("*");
				if (creatureConfig == null) {
					return null;
				}
			}
		}

		return creatureConfig;
	}

	private static ItemStack getStack (Random rand, String boneType, ConfigCreatureLoot lootConfig) {
		if (lootConfig.chance < rand.nextDouble()) {
			return null;
		}

		ConfigBoneType boneConfig = CONFIG_BONE_TYPES.get(boneType);

		if (boneConfig == null) return null;

		return boneConfig.getBoneStack(new Range(lootConfig.min, lootConfig.max).getInt(rand));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onMobXp (LivingExperienceDropEvent event) {

		// first we check if we should even try to do drops
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null || !(entity instanceof EntityLiving)) return;

		// then we check if there's a config for this entity
		ConfigCreature config = getCreatureConfig(entity);
		if (config == null) return;

		boolean wasSummoned = entity.getEntityData().getByte("soulus:spawn_whitelisted") == (byte) 2;
		String spawnType = wasSummoned ? "summoned" : "spawned";

		ConfigCreatureDrops dropConfig = config.drops.get(spawnType);
		ConfigCreatureDrops dropConfigAll = config.drops.get("all");

		// then we check if drops are configured for this entity
		if (dropConfig == null && dropConfigAll == null) return;

		boolean dc = dropConfig != null;
		boolean dca = dropConfigAll != null;

		// check if it's configured not to have xp in this situation
		if (!(dc ? dropConfig.hasXp : dca ? dropConfigAll.hasXp : true)) {
			event.setCanceled(true);
		}
	}
}
