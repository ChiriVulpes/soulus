package yuudaari.soulus.common.util;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;

public class XP {

	public static void grant (final EntityPlayer player, int amount) {
		if (player.world.isRemote)
			return;

		while (amount > 0) {
			int splitXp = EntityXPOrb.getXPSplit(amount);
			amount -= splitXp;
			player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, splitXp));
		}
	}
}
