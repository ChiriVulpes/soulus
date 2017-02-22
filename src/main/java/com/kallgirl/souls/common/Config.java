package com.kallgirl.souls.common;

import com.kallgirl.souls.common.util.MobTarget;
import com.kallgirl.souls.common.util.NBTBuilder;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Config {

	public static abstract class SpecialSpawnInfo {
		public abstract String getEntityName ();
		public NBTBuilder getEntityNBT () { return null; }
		public void modifyEntity (EntityLiving entity) {}
	}

	public static Map<String, String> EntityIdMap = new HashMap<>();
	static {
		EntityIdMap.put("ZombiePigman", "PigZombie");
		EntityIdMap.put("Ocelot", "Ozelot");
		EntityIdMap.put("Horse", "EntityHorse");
	}

	public static class ColourInfo {
		public int primaryColour;
		public int secondaryColour;
		public ColourInfo(int primaryColour, int secondaryColour) {
			this.primaryColour = primaryColour;
			this.secondaryColour = secondaryColour;
		}
		public ColourInfo(EntityList.EntityEggInfo eggInfo) {
			this.primaryColour = eggInfo.primaryColor;
			this.secondaryColour = eggInfo.secondaryColor;
		}
	}

	public static class SoulInfo {
		public int dropChance;
		public int neededForSoul = 16;
		public ColourInfo colourInfo = null;
		public SpecialSpawnInfo specialSpawnInfo = null;

		public SoulInfo (int dropChance) {
			this.dropChance = dropChance;
		}
		public SoulInfo (int dropChance, int neededForSoul) {
			this(dropChance);
			this.neededForSoul = neededForSoul;
		}
		public SoulInfo (int dropChance, int neededForSoul, ColourInfo colourInfo, SpecialSpawnInfo specialSpawnInfo) {
			this(dropChance, neededForSoul);
			this.colourInfo = colourInfo;
			this.specialSpawnInfo = specialSpawnInfo;
		}
	}

	public static Map<BoneType, Map<String, SoulInfo>> spawnMap = new HashMap<>();
	static {

		// NORMAL
		Map<String, SoulInfo> spawnMapNormal = new HashMap<>();
		spawnMap.put(BoneType.NORMAL, spawnMapNormal);

		// passive mobs
		spawnMapNormal.put("none", new SoulInfo(5));
		spawnMapNormal.put("Pig", new SoulInfo(10, 16));
		spawnMapNormal.put("Chicken", new SoulInfo(10, 16));
		spawnMapNormal.put("Cow", new SoulInfo(8, 16));
		spawnMapNormal.put("Sheep", new SoulInfo(8, 16));
		spawnMapNormal.put("Wolf", new SoulInfo(6, 16));
		spawnMapNormal.put("PolarBear", new SoulInfo(4, 16));
		spawnMapNormal.put("Rabbit", new SoulInfo(4, 16));
		spawnMapNormal.put("Bat", new SoulInfo(4, 16));
		spawnMapNormal.put("Villager", new SoulInfo(1, 16));
		spawnMapNormal.put("Ocelot", new SoulInfo(4, 16));
		spawnMapNormal.put("Skeleton", new SoulInfo(3, 16));
		spawnMapNormal.put("Zombie", new SoulInfo(2, 16));
		spawnMapNormal.put("Horse", new SoulInfo(1, 16));


		// NETHER
		Map<String, SoulInfo> spawnMapNether = new HashMap<>();
		spawnMap.put(BoneType.NETHER, spawnMapNether);

		// aggressive mobs
		spawnMapNether.put("none", new SoulInfo(10));
		spawnMapNether.put("ZombiePigman", new SoulInfo(8, 16));
		spawnMapNether.put("Spider", new SoulInfo(7, 16));
		spawnMapNether.put("CaveSpider", new SoulInfo(3, 16));
		spawnMapNether.put("Blaze", new SoulInfo(3, 16));
		spawnMapNether.put("WitherSkeleton", new SoulInfo(
			1, 16,
			new ColourInfo(0x333030, 0x191313),
			new SpecialSpawnInfo(){
				public String getEntityName () {
					return "Skeleton";
				}
				@Override
				public void modifyEntity (EntityLiving entityIn) {
					EntitySkeleton entity = (EntitySkeleton) entityIn;
					entity.setSkeletonType(SkeletonType.WITHER);
					entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
					entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
					entity.setCombatTask();
				}
			}
		));
		spawnMapNether.put("Witch", new SoulInfo(1, 16));
		spawnMapNether.put("Ghast", new SoulInfo(1, 16));


		// ENDER
		Map<String, SoulInfo> spawnMapEnder = new HashMap<>();
		spawnMap.put(BoneType.ENDER, spawnMapEnder);

		spawnMapEnder.put("none", new SoulInfo(20));
		spawnMapEnder.put("Endermite", new SoulInfo(6, 16));
		spawnMapEnder.put("Creeper", new SoulInfo(3, 16));
		spawnMapEnder.put("Enderman", new SoulInfo(2, 16));
		spawnMapEnder.put("Shulker", new SoulInfo(1, 16));


		// SCALE
		Map<String, SoulInfo> spawnMapScale = new HashMap<>();
		spawnMap.put(BoneType.SCALE, spawnMapScale);

		spawnMapScale.put("none", new SoulInfo(20));
		spawnMapScale.put("Squid", new SoulInfo(6, 16));
		spawnMapScale.put("Guardian", new SoulInfo(2, 16));
		spawnMapScale.put("Silverfish", new SoulInfo(1, 16));


		// MISC
		Map<String, SoulInfo> spawnMapMisc = new HashMap<>();
		spawnMap.put(BoneType.MISC, spawnMapMisc);

		spawnMapMisc.put("Slime", new SoulInfo(0, 16));
		spawnMapMisc.put("MagmaCube", new SoulInfo(0, 16));
		spawnMapMisc.put("Mooshroom", new SoulInfo(0, 16));
	}

	@Nonnull
	public static SoulInfo getSoulInfo (@Nonnull String mobTarget) {
		SoulInfo result = getSoulInfo(mobTarget, true);
		if (result == null) throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		return result;
	}
	@Nullable
	public static SoulInfo getSoulInfo (@Nonnull String mobTarget, boolean err) {
		mobTarget = MobTarget.fixMobTarget(mobTarget);
		SoulInfo result = spawnMap.get(BoneType.NORMAL).get(mobTarget);
		if (result == null) result = spawnMap.get(BoneType.NETHER).get(mobTarget);
		if (result == null) result = spawnMap.get(BoneType.ENDER).get(mobTarget);
		if (result == null) result = spawnMap.get(BoneType.SCALE).get(mobTarget);
		if (result == null && err) {
			throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		}
		return result;
	}
}
