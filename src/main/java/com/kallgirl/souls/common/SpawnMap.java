package com.kallgirl.souls.common;

import java.util.HashMap;
import java.util.Map;

public class SpawnMap {
	public enum BoneType {
		NORMAL,
		WITHER,
		ENDER,
		SCALE,
		IRON
	}
	public static class SpawnInfo {
		public int chance;
		public BoneType boneType;
		public int required;
		public SpawnInfo(int chance, BoneType boneType, int required) {
			this.chance = chance;
			this.boneType = boneType;
			this.required = required;
		}
	}
	public static Map<String, SpawnInfo> map = new HashMap<>();
	static {
		// passive mobs
		map.put("Pig", new SpawnInfo(10, BoneType.NORMAL, 16));
		map.put("Chicken", new SpawnInfo(10, BoneType.NORMAL, 16));
		map.put("Sheep", new SpawnInfo(8, BoneType.NORMAL, 16));
		map.put("Cow", new SpawnInfo(8, BoneType.NORMAL, 16));
		map.put("Ocelot", new SpawnInfo(4, BoneType.NORMAL, 16));
		map.put("Rabbit", new SpawnInfo(4, BoneType.NORMAL, 16));
		map.put("Villager", new SpawnInfo(4, BoneType.NORMAL, 16));
		map.put("MushroomCow", new SpawnInfo(1, BoneType.NORMAL, 16));

		// neutral mobs
		map.put("Wolf", new SpawnInfo(6, BoneType.NORMAL, 16));
		map.put("PolarBear", new SpawnInfo(4, BoneType.NORMAL, 16));

		// aggressive mobs
		map.put("Zombie", new SpawnInfo(2, BoneType.NORMAL, 16));
		map.put("Skeleton", new SpawnInfo(3, BoneType.NORMAL, 16));
		map.put("Witch", new SpawnInfo(1, BoneType.NORMAL, 16));
	}
}
