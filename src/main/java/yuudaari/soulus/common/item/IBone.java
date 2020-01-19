package yuudaari.soulus.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.Config;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.registration.item.IRightClickableItem;

public interface IBone extends IRightClickableItem {

	ResourceLocation getRegistryName ();

	default void onRightClickEntity (final Entity entity, final ItemStack stack, final EntityPlayer player) {
		if (!(entity instanceof EntityWolf))
			return;

		final EntityWolf wolf = (EntityWolf) entity;
		if (wolf.isTamed() || wolf.isAngry())
			return;

		if (!player.capabilities.isCreativeMode)
			stack.shrink(1);

		if (player.world.isRemote)
			return;

		feedToWolf(wolf, stack, player);
		final ConfigBoneType CONFIG = Config.get(Soulus.MODID, ConfigBoneTypes.class)
			.getFromBone(getRegistryName().toString());

		if (player.world.rand.nextDouble() < CONFIG.tameChance && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(wolf, player)) {
			wolf.setTamedBy(player);
			wolf.navigator.clearPath();
			wolf.setAttackTarget(null);
			wolf.aiSit.setSitting(true);
			wolf.setHealth(20.0F);
			wolf.playTameEffect(true);
			wolf.world.setEntityState(wolf, (byte) 7);
		} else {
			wolf.playTameEffect(false);
			wolf.world.setEntityState(wolf, (byte) 6);

			final ConfigBoneTypes CONFIG_BONE_TYPES = Config.get(Soulus.MODID, ConfigBoneTypes.class);
			final ConfigBoneType configBoneType = CONFIG_BONE_TYPES.getFromBone(stack.getItem().getRegistryName().toString());

			SPacketTitle spackettitle2 = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentTranslation("message.actionbar." + Soulus.MODID + ":feed_wolf_bone." + configBoneType.name.toLowerCase()));
			((EntityPlayerMP) player).connection.sendPacket(spackettitle2);
		}
	}

	default void feedToWolf (final EntityWolf wolf, final ItemStack stack, final EntityPlayer player) {
	}
}
