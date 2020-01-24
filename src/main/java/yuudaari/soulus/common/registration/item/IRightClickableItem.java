package yuudaari.soulus.common.registration.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public interface IRightClickableItem {

	@SubscribeEvent
	public static void onEntityRightClick (final EntityInteract event) {
		final net.minecraft.item.Item item = event.getItemStack().getItem();
		if (((Object) item) instanceof IRightClickableItem) {
			((IRightClickableItem) (Object) item).onRightClickEntity(event.getTarget(), event.getItemStack(), event.getEntityPlayer());
		}
	}

	default void onRightClickEntity (final Entity entity, final ItemStack stack, final EntityPlayer player) {
	}
}
