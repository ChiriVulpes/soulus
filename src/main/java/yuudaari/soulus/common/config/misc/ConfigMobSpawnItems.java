package yuudaari.soulus.common.config.misc;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.CollectionSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/mob_spawn_items", id = Soulus.MODID)
@Serializable
public class ConfigMobSpawnItems {

	@Serialized(CollectionSerializer.OfStrings.class) public List<String> mobSpawnItems = Lists.newArrayList(new String[] {
		"minecraft:spawn_egg",
		"thermalexpansion:morb",
		"enderio:item_soul_vial",
		"extrautilities:golden_lasso",
		"industrialforegoing:mob_imprisonment_tool",
	});

	public boolean isSpawningItem (final ItemStack stack) {
		return this.mobSpawnItems.contains(stack.getItem().getRegistryName().toString());
	}
}
