package yuudaari.soulus.common.block.skewer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSkewer;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.skewer.Skewer.Upgrade;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.misc.ModDamageSource;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.TetherEntity;
import yuudaari.soulus.common.util.ModPotionEffect;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

@Mod.EventBusSubscriber
@ConfigInjected(Soulus.MODID)
public class SkewerTileEntity extends UpgradeableBlockTileEntity {

	public int crystalBloodContainedBlood = 0;

	private Map<EntityLivingBase, Long> entityHitTimes = new HashMap<>();

	@Override
	public Skewer getBlock () {
		return ModBlocks.SKEWER;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	private DamageSource getDamageSource () {
		if (!world.isRemote && upgrades.get(Upgrade.PLAYER) > 0) {
			return ModDamageSource.getSkewerPlayer((WorldServer) world);

		} else {
			return ModDamageSource.SKEWER;
		}
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigSkewer CONFIG;

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void update () {
		if (world.isRemote)
			return;

		IBlockState state = world.getBlockState(pos);
		if (state.getValue(Skewer.EXTENDED)) {
			EnumFacing facing = state.getValue(Skewer.FACING);
			long time = world.getTotalWorldTime();

			entityHitTimes.entrySet()
				.removeIf(entityHitTime -> time - entityHitTime.getValue() > CONFIG.ticksBetweenDamage);

			for (EntityLivingBase entity : world
				.getEntitiesWithinAABB(EntityLivingBase.class, Skewer.getSpikeHitbox(facing, pos))) {

				if (!entity.getIsInvulnerable() && !entityHitTimes.containsKey(entity)) {

					entityHitTimes.put(entity, world.getTotalWorldTime());

					float damage = CONFIG.baseDamage;

					damage += CONFIG.upgradeDamageEffectiveness * upgrades.get(Upgrade.DAMAGE);

					int rtime = entity.hurtResistantTime;
					entity.attackEntityFrom(getDamageSource(), damage);
					entity.hurtResistantTime = rtime;

					if (upgrades.get(Upgrade.POISON) > 0 && world.rand.nextFloat() < CONFIG.poisonChance
						.get(upgrades.get(Upgrade.POISON))) {

						for (ModPotionEffect effect : CONFIG.poisonEffects)
							effect.apply(entity);
					}

					if (upgrades.get(Upgrade.TETHER) > 0 && world.rand.nextFloat() < CONFIG.tetherChance
						.get(upgrades.get(Upgrade.TETHER))) {

						tetherEntity(entity);
						SoulsPacketHandler.INSTANCE
							.sendToAllAround(new TetherEntity(entity), new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 256));

					} else if (entity instanceof EntityEnderman && !entity.getEntityData().hasKey("soulus:tethered")) {
						((EntityEnderman) entity).teleportRandomly();
					}

					if (world.rand.nextFloat() < CONFIG.chanceForBloodPerHit && upgrades
						.get(Upgrade.CRYSTAL_BLOOD) > 0) {

						crystalBloodContainedBlood += CONFIG.bloodPerDamage * damage;
						if (crystalBloodContainedBlood > CrystalBlood.CONFIG.requiredBlood) {
							crystalBloodContainedBlood = CrystalBlood.CONFIG.requiredBlood;
						}
						CrystalBlood.bloodParticles(entity);
						blockUpdate();
					}
				}
			}
		}
	}

	public static void tetherEntity (EntityLivingBase entity) {
		entity.getEntityData().setByte("soulus:tethered", (byte) 0);
	}

	@Override
	public void onInsertUpgrade (ItemStack stack, IUpgrade upgrade, int newQuantity) {
		if (upgrade == Upgrade.CRYSTAL_BLOOD) {
			this.crystalBloodContainedBlood = CrystalBlood.getContainedBlood(stack);

		} else if (upgrades.get(Upgrade.CRYSTAL_BLOOD) > 0) {
			// always keep blood crystal at the top (first to remove)
			this.insertionOrder.remove(Upgrade.CRYSTAL_BLOOD);
			this.insertionOrder.push(Upgrade.CRYSTAL_BLOOD);
		}

		if (upgrade == Upgrade.POWER) {
			getBlock().updateExtendedState(world.getBlockState(pos), world, pos);
		}
	}

	@Override
	public int removeUpgrade (IUpgrade upgrade) {
		int result = super.removeUpgrade(upgrade);

		if (upgrade == Upgrade.POWER && result > 0) {
			getBlock().updateExtendedState(world.getBlockState(pos), world, pos);
		}

		return result;
	}

	@SubscribeEvent
	public static void onEnderTeleport (EnderTeleportEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity != null && !(entity instanceof EntityPlayer)) {
			if (entity.getEntityData().hasKey("soulus:tethered")) {
				event.setCanceled(true);
			}
		}
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		crystalBloodContainedBlood = compound.getInteger("crystal_blood_stored_blood");
	}

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		compound.setInteger("crystal_blood_stored_blood", crystalBloodContainedBlood);
	}

}
