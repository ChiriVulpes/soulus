package yuudaari.souls.common;

import yuudaari.souls.common.util.BoneType;
import yuudaari.souls.common.util.NBTHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Config {

	public static abstract class SpecialSpawnInfo {
		public abstract String getEntityName();

		public NBTHelper getEntityNBT() {
			return null;
		}

		public void modifyEntity(EntityLiving entity) {
		}
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

		public SoulInfo(int dropChance) {
			this.dropChance = dropChance;
		}

		public SoulInfo(int dropChance, int neededForSoul) {
			this(dropChance);
			this.neededForSoul = neededForSoul;
		}

		public SoulInfo(int dropChance, int neededForSoul, ColourInfo colourInfo, SpecialSpawnInfo specialSpawnInfo) {
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
		spawnMapNormal.put("pig", new SoulInfo(10, 16));
		spawnMapNormal.put("chicken", new SoulInfo(10, 16));
		spawnMapNormal.put("cow", new SoulInfo(8, 16));
		spawnMapNormal.put("sheep", new SoulInfo(8, 16));
		spawnMapNormal.put("wolf", new SoulInfo(6, 16));
		spawnMapNormal.put("polar_bear", new SoulInfo(4, 16));
		spawnMapNormal.put("rabbit", new SoulInfo(4, 16));
		spawnMapNormal.put("bat", new SoulInfo(4, 16));
		spawnMapNormal.put("villager", new SoulInfo(1, 16));
		spawnMapNormal.put("ocelot", new SoulInfo(4, 16));
		spawnMapNormal.put("skeleton", new SoulInfo(3, 16));
		spawnMapNormal.put("zombie", new SoulInfo(2, 16));
		spawnMapNormal.put("horse", new SoulInfo(1, 16));

		// NETHER
		Map<String, SoulInfo> spawnMapNether = new HashMap<>();
		spawnMap.put(BoneType.NETHER, spawnMapNether);

		// aggressive mobs
		spawnMapNether.put("none", new SoulInfo(10));
		spawnMapNether.put("zombie_pigman", new SoulInfo(8, 16));
		spawnMapNether.put("spider", new SoulInfo(7, 16));
		spawnMapNether.put("cave_spider", new SoulInfo(3, 16));
		spawnMapNether.put("blaze", new SoulInfo(3, 16));
		spawnMapNether.put("wither_skeleton", new SoulInfo(1, 16));
		spawnMapNether.put("witch", new SoulInfo(1, 16));
		spawnMapNether.put("ghast", new SoulInfo(1, 16));

		// ENDER
		Map<String, SoulInfo> spawnMapEnder = new HashMap<>();
		spawnMap.put(BoneType.ENDER, spawnMapEnder);

		spawnMapEnder.put("none", new SoulInfo(20));
		spawnMapEnder.put("endermite", new SoulInfo(6, 16));
		spawnMapEnder.put("creeper", new SoulInfo(3, 16));
		spawnMapEnder.put("enderman", new SoulInfo(2, 16));
		spawnMapEnder.put("shulker", new SoulInfo(1, 16));

		// SCALE
		Map<String, SoulInfo> spawnMapScale = new HashMap<>();
		spawnMap.put(BoneType.SCALE, spawnMapScale);

		spawnMapScale.put("none", new SoulInfo(20));
		spawnMapScale.put("squid", new SoulInfo(6, 16));
		spawnMapScale.put("guardian", new SoulInfo(2, 16));
		spawnMapScale.put("silverfish", new SoulInfo(1, 16));

		// MISC
		Map<String, SoulInfo> spawnMapMisc = new HashMap<>();
		spawnMap.put(BoneType.MISC, spawnMapMisc);

		spawnMapMisc.put("slime", new SoulInfo(0, 16));
		spawnMapMisc.put("magmacube", new SoulInfo(0, 16));
		spawnMapMisc.put("mooshroom", new SoulInfo(0, 16));
	}

	@Nonnull
	public static SoulInfo getSoulInfo(@Nonnull String mobTarget) {
		SoulInfo result = getSoulInfo(mobTarget, true);
		if (result == null)
			throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		return result;
	}

	@Nullable
	public static SoulInfo getSoulInfo(@Nonnull String mobTarget, boolean err) {
		SoulInfo result = spawnMap.get(BoneType.NORMAL).get(mobTarget);
		if (result == null)
			result = spawnMap.get(BoneType.NETHER).get(mobTarget);
		if (result == null)
			result = spawnMap.get(BoneType.ENDER).get(mobTarget);
		if (result == null)
			result = spawnMap.get(BoneType.SCALE).get(mobTarget);
		if (result == null && err) {
			throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		}
		return result;
	}
}
