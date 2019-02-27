package yuudaari.soulus.common.misc;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.misc.ConfigBarkFromLogs;

@Mod.EventBusSubscriber
@ConfigInjected(Soulus.MODID)
public class BarkFromLogs {

	@Inject public static ConfigBarkFromLogs CONFIG;

	@SubscribeEvent
	public static void onHarvest (HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			if (CONFIG.logWhitelist.contains(event.getState().getBlock().getRegistryName().toString())) {
				if (event.getWorld().rand.nextFloat() < CONFIG.barkChance) {
					List<ItemStack> drops = event.getDrops();
					drops.clear();
					drops.add(ItemRegistry.BARK.getItemStack(8));
				}
			}
		}
	}
}
