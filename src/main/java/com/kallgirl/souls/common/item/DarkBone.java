package com.kallgirl.souls.common.item;

import net.minecraft.item.ItemStack;
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

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;

public class DarkBone extends Item {
	
	public DarkBone() {
		super("darkBone");
		addOreDict("boneWither");
	}

	@ObjectHolder("tconstruct:materials")
	public static final net.minecraft.item.Item tinkersMaterial = null;

	@Override
	public void init() {
		super.init();
		if (Loader.isModLoaded("tconstruct")) {
			// add tinkers's necrotic bones to the boneWither ore dictionary
			ItemStack necroticBone = new ItemStack(tinkersMaterial, 1, 17);
			OreDictionary.registerOre("boneWither", necroticBone);
		}
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.ENTITIES_WITHER_SKELETON)) {
			if (!Loader.isModLoaded("tconstruct")) {
				// tinkers's isn't in the pack, so we have to add in our wither bones
				LootCondition[] lootConditions = new LootCondition[0];

				LootEntry entry = new LootEntryItem(
					this, 1, 0,
					new LootFunction[] {
						new SetMetadata(lootConditions, new RandomValueRange(0F))
					},
					lootConditions,
					"boneWither"
				);
				
				event.getTable().addPool(new LootPool(
					new LootEntry[]{entry},
					new LootCondition[]{
						new KilledByPlayer(false),
						new RandomChanceWithLooting(0.07F, 0.05f)
					},
					new RandomValueRange(1),
					new RandomValueRange(0),
					"boneWither"
				));
			}
		}
	}
}