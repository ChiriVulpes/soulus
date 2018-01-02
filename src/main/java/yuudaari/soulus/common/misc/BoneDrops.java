package yuudaari.soulus.common.misc;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yuudaari.soulus.common.config.EssenceConfig;
import yuudaari.soulus.common.config.EssenceConfig.CreatureLootConfig;
import yuudaari.soulus.common.misc.NoMobSpawning.NoMobSpawningDimensionConfig;
import yuudaari.soulus.common.misc.NoMobSpawning.NoMobSpawningDimensionConfig.NoMobSpawningBiomeConfig;
import yuudaari.soulus.common.misc.NoMobSpawning.NoMobSpawningDimensionConfig.NoMobSpawningBiomeConfig.NoMobSpawningCreatureConfig;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber
public class BoneDrops {
	@SubscribeEvent
	public static void onMobDrops(LivingDropsEvent event) {

		// first we check if we should even try to do drops
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null || !(entity instanceof EntityLiving))
			return;

		List<EntityItem> drops = event.getDrops();

		// then we check to see if the entity was summoned
		if (entity.getEntityData().getByte("soulus:spawn_whitelisted") == (byte) 2) {
			doMobDrops(drops, entity);
			return;
		}

		// then we get the dimension config for this potential spawn
		DimensionType dimension = entity.world.provider.getDimensionType();
		//Logger.info(dimension.getName());
		NoMobSpawningDimensionConfig dimensionConfig = NoMobSpawning.INSTANCE.dimensionConfigs.get(dimension.getName());
		if (dimensionConfig == null) {
			dimensionConfig = NoMobSpawning.INSTANCE.dimensionConfigs.get("*");
			if (dimensionConfig == null) {
				doMobDrops(drops, entity);
				return;
			}
		}

		// then we get the biome config for this potential spawn
		BlockPos pos = entity.getPosition();
		Biome biome = entity.world.getBiome(pos);
		//Logger.info(biome.getRegistryName().toString());
		NoMobSpawningBiomeConfig biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().toString());
		if (biomeConfig == null) {
			biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().getResourceDomain() + ":*");
			if (biomeConfig == null) {
				biomeConfig = dimensionConfig.biomeConfigs.get("*");
				if (biomeConfig == null) {
					doMobDrops(drops, entity);
					return;
				}
			}
		}

		// then we get the creature config for this potential spawn
		String entityName = EntityList.getKey(entity).toString();
		//Logger.info(entityName);
		NoMobSpawningCreatureConfig creatureConfig = biomeConfig.creatureConfigs.get(entityName);
		if (creatureConfig == null) {
			creatureConfig = biomeConfig.creatureConfigs
					.get(new ResourceLocation(entityName).getResourceDomain() + ":*");
			if (creatureConfig == null) {
				creatureConfig = biomeConfig.creatureConfigs.get("*");
				if (creatureConfig == null) {
					doMobDrops(drops, entity);
					return;
				}
			}
		}

		if (creatureConfig.hasDrops) {
			doMobDrops(drops, entity);
		} else {
			drops.clear();
		}
	}

	private static void doMobDrops(List<EntityItem> drops, EntityLivingBase entity) {
		for (EssenceConfig essenceConfig : Soulus.config.essences) {
			for (Map.Entry<String, CreatureLootConfig> lootConfig : essenceConfig.loot.entrySet()) {
				ResourceLocation name = EntityList.getKey(entity);
				if (name != null && lootConfig.getKey().equals(name.toString())) {
					ItemStack stack = getStack(entity.world.rand, essenceConfig.bones.type, lootConfig.getValue());
					if (stack != null) {
						drops.add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack));
					}
					break;
				}
			}
		}
	}

	private static ItemStack getStack(Random rand, BoneType boneType, CreatureLootConfig lootConfig) {
		if (lootConfig.chance < rand.nextDouble()) {
			return null;
		}

		Item item;
		if (boneType == BoneType.NORMAL) {
			item = ModItems.BONE_NORMAL;
		} else if (boneType == BoneType.DRY) {
			item = ModItems.BONE_DRY;
		} else if (boneType == BoneType.FUNGAL) {
			item = ModItems.BONE_FUNGAL;
		} else if (boneType == BoneType.FROZEN) {
			item = ModItems.BONE_FROZEN;
		} else if (boneType == BoneType.NETHER) {
			item = ModItems.BONE_NETHER;
		} else if (boneType == BoneType.ENDER) {
			item = ModItems.BONE_ENDER;
		} else if (boneType == BoneType.SCALE) {
			item = ModItems.BONE_SCALE;
		} else {
			return null;
		}

		ItemStack result = new ItemStack(item);
		result.setCount(new Range(lootConfig.min, lootConfig.max).getInt(rand));
		return result;
	}
}