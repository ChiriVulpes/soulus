package yuudaari.soulus.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigEmeraldBloody;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.registration.Registration;

@Mod.EventBusSubscriber
@ConfigInjected(Soulus.MODID)
public class EmeraldBloody extends Registration.Item {

	@Inject public static ConfigEmeraldBloody CONFIG;

	public EmeraldBloody () {
		super("emerald_coated");
		setHasDescription();
	}

	@SubscribeEvent
	public static void onEntityDeath (final LivingDropsEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityVillager))
			return;

		final int dropAmount = CONFIG.villagerDropAmount.getInt(entity.world.rand);
		if (dropAmount > 0)
			event.getDrops()
				.add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, ItemRegistry.EMERALD_COATED.getItemStack(dropAmount)));
	}
}
