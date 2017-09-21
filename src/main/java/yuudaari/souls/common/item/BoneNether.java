package yuudaari.souls.common.item;

import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import yuudaari.souls.common.util.ModItem;

@Mod.EventBusSubscriber
public class BoneNether extends ModItem {

	@ObjectHolder("tconstruct:materials")
	public static final Item tinkersMaterial = null;

	public BoneNether() {
		super("bone_nether");
		addOreDict("boneWithered");
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.ENTITIES_WITHER_SKELETON)) {
			if (!Loader.isModLoaded("tconstruct")) {
				// tinkers isn't in the pack, so we have to add in our wither bones
				LootCondition[] lootConditions = new LootCondition[0];

				LootEntry entry = new LootEntryItem(this, 1, 0,
						new LootFunction[] { new SetMetadata(lootConditions, new RandomValueRange(0F)) },
						lootConditions, "bone_wither");

				event.getTable().addPool(new LootPool(new LootEntry[] { entry },
						new LootCondition[] { new KilledByPlayer(false), new RandomChanceWithLooting(0.07F, 0.05f) },
						new RandomValueRange(1), new RandomValueRange(0), "bone_wither"));
			}
		}
	}
}