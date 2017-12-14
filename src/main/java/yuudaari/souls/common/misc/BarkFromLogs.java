package yuudaari.souls.common.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.souls.common.ModItems;
import yuudaari.souls.common.config.Config;
import yuudaari.souls.common.config.ManualSerializer;
import yuudaari.souls.common.config.Serializer;

@Mod.EventBusSubscriber
public class BarkFromLogs {
	public static BarkFromLogs INSTANCE = new BarkFromLogs();

	public static Serializer<BarkFromLogs> serializer = new Serializer<>(BarkFromLogs.class, "barkChance");
	static {
		serializer.fieldHandlers.put("logWhitelist",
				new ManualSerializer(Config::serializeList, Config::deserializeList));
	}

	public float barkChance = 0.01f;
	public List<String> logWhitelist = new ArrayList<>();
	{
		logWhitelist.add(Blocks.LOG.getRegistryName().toString());
		logWhitelist.add(Blocks.LOG2.getRegistryName().toString());
	}

	@SubscribeEvent
	public static void onHarvest(HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			if (INSTANCE.logWhitelist.contains(event.getState().getBlock().getRegistryName().toString())) {
				if (event.getWorld().rand.nextFloat() < INSTANCE.barkChance) {
					List<ItemStack> drops = event.getDrops();
					drops.clear();
					drops.add(ModItems.BARK.getItemStack(8));
				}
			}
		}
	}
}