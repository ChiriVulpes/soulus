package yuudaari.soulus.common.misc;

import java.util.Map;
import java.util.Random;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.CreatureConfig;
import yuudaari.soulus.common.config.CreatureConfig.CreatureLootConfig;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.Range;

@Mod.EventBusSubscriber
public class BoneDrops {
	@SubscribeEvent
	public static void onMobDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		for (CreatureConfig creatureConfig : Soulus.config.creatures) {
			for (Map.Entry<String, CreatureLootConfig> lootConfig : creatureConfig.loot.entrySet()) {
				if (lootConfig.getKey().equals(EntityList.getKey(entity).toString())) {
					ItemStack stack = getStack(entity.world.rand, creatureConfig.bones.type, lootConfig.getValue());
					if (stack != null) {
						event.getDrops()
								.add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack));
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
			item = Items.BONE;
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