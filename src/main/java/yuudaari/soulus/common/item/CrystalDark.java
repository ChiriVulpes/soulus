package yuudaari.soulus.common.item;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigCrystalDark;
import yuudaari.soulus.common.misc.ModDamageSource;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.CrystalDarkPrick;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.XP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ConfigInjected(Soulus.MODID)
public class CrystalDark extends Registration.Item {

	@Inject public static ConfigCrystalDark CONFIG;

	public CrystalDark () {
		super("crystal_dark");
		setHasDescription();

		Soulus.onConfigReload( () -> setMaxStackSize(CONFIG.stackSize));
	}

	/**
	 * This won't ever be called by Soulus, but a modpack might add a crafting recipe for them, so we give them XP the same as
	 * any other upgrade.
	 */
	@Override
	public void onCreated (final ItemStack stack, final World world, final EntityPlayer player) {
		if (player == null)
			return;

		for (int i = 0; i < stack.getCount(); i++)
			XP.grant(player, CONFIG.xp.getInt(world.rand));
	}


	@Override
	public void onUpdate (ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!world.isRemote && entityIn instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) entityIn;

			// don't try to attack the player if they're in creative mode
			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return;

			for (int i = 0; i < stack.getCount(); i++) {
				for (ModPotionEffect effect : CONFIG.heldEffects)
					effect.apply(entity);

				if (world.rand.nextFloat() <= CONFIG.prickChance) {
					if (CONFIG.prickAmount > 0) {
						entity.attackEntityFrom(ModDamageSource.CRYSTAL_DARK, CONFIG.prickAmount);
					}

					for (ModPotionEffect effect : CONFIG.prickEffects)
						effect.apply(entity);

					SoulsPacketHandler.INSTANCE
						.sendToAllAround(new CrystalDarkPrick(entity), new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 128));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void particles (EntityLivingBase entity) {
		World world = entity.getEntityWorld();
		Random rand = world.rand;

		for (int i = 0; i < CONFIG.particleCount; ++i) {
			double d3 = (entity.posX - 0.5F + rand.nextFloat());
			double d4 = (entity.posY + rand.nextFloat());
			double d5 = (entity.posZ - 0.5F + rand.nextFloat());
			double d3o = (d3 - entity.posX) / 5;
			double d4o = (d4 - entity.posY) / 5;
			double d5o = (d5 - entity.posZ) / 5;
			world.spawnParticle(ParticleType.CRYSTAL_DARK.getId(), false, d3, d4, d5, d3o, d4o, d5o, 1);
		}
	}
}
