package yuudaari.soulus.common.compat;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class Patchouli {

	@SubscribeEvent
	public static void onAdvancement (AdvancementEvent event) {
		if (!event.getAdvancement().getId().toString().equals("soulus:root")) return;

		final ItemStack drop = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("patchouli:guide_book")));
		final NBTTagCompound dropNbt = new NBTTagCompound();
		dropNbt.setString("patchouli:book", "soulus:soulus");
		drop.setTagCompound(dropNbt);

		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();

		final EntityItem dropEntity = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
		dropEntity.setNoPickupDelay();
		world.spawnEntity(dropEntity);

		event.getEntityPlayer();
	}
}
