package com.kallgirl.souls.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class NoMobSpawning {
	@SubscribeEvent
	public static void catchPotentialSpawns(WorldEvent.PotentialSpawns event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onMobJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity == null || !(entity instanceof EntityLiving) || event.getWorld().isRemote) return;
		NBTTagCompound entityData = entity.getEntityData();
		if (!entityData.hasKey("souls:spawned-by-souls", 1)) {
			event.setCanceled(true);
		}
	}
}
