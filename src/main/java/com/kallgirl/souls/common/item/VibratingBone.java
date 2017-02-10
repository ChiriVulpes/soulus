package com.kallgirl.souls.common.item;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.LootTableLoadEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VibratingBone extends Item {

	public VibratingBone() {
		super("vibratingBone");
		glint = true;
		addOreDict("boneEnder");
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.ENTITIES_ENDERMAN)) {
			LootCondition[] lootConditions = new LootCondition[0];

			LootEntry entry = new LootEntryItem(
               this, 1, 0,
               new LootFunction[] {
                   new SetMetadata(lootConditions, new RandomValueRange(0F))
               },
               lootConditions,
               "boneEnder"
			);

			event.getTable().addPool(new LootPool(
                 new LootEntry[]{entry},
                 new LootCondition[]{
                     new KilledByPlayer(false),
                     new RandomChanceWithLooting(0.07F, 0.05f)
                 },
                 new RandomValueRange(1),
                 new RandomValueRange(0),
                 "boneEnder"
			));
		}
	}
}