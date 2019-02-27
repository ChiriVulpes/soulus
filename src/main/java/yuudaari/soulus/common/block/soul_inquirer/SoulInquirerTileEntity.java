package yuudaari.soulus.common.block.soul_inquirer;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.block.soul_inquirer.SoulInquirer.Upgrade;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSoulInquirer;
import yuudaari.soulus.common.config.essence.ConfigEssences;

@ConfigInjected(Soulus.MODID)
public class SoulInquirerTileEntity extends UpgradeableBlockTileEntity implements ITickable {

	@Inject public static ConfigSoulInquirer CONFIG;
	@Inject public static ConfigEssences CONFIG_ESSENCES;

	@Override
	public SoulInquirer getBlock () {
		return BlockRegistry.SOUL_INQUIRER;
	}

	/* OTHER */
	private boolean hasInit = false;
	private String essenceType;

	private int activatingRange;

	private int signalStrength;

	/* RENDERER */
	private double timeTillParticle = 0;
	public double mobRotation;
	public double prevMobRotation;
	public EntityLiving renderMob;

	public void reset () {
		this.renderMob = null;
		blockUpdate();
	}

	private void onUpdateUpgrades () {
		onUpdateUpgrades(false);
	}

	@Override
	public void onUpdateUpgrades (boolean readFromNBT) {
		if (isInvalid()) {
			Soulus.removeConfigReloadHandler(this::onUpdateUpgrades);
			return;
		}

		Soulus.onConfigReload(this::onUpdateUpgrades);

		int rangeUpgrades = upgrades.get(Upgrade.RANGE);
		activatingRange = CONFIG.nonUpgradedRange + rangeUpgrades * CONFIG.upgradeRangeEffectiveness;

		if (world != null && !world.isRemote) {
			blockUpdate();
		}

	}

	public String lastRenderedEssenceType;

	public String getEssenceType () {
		return essenceType;
	}

	public void setEssenceType (String essenceType) {
		this.essenceType = essenceType;
	}

	public int getSignalStrength () {
		return signalStrength;
	}

	public NBTTagCompound getEntityNbt () {
		NBTTagCompound result = new NBTTagCompound();
		result.setString("id", getSpawnMob());
		result.setByte("PersistenceRequired", (byte) 1);
		return result;
	}

	private String getSpawnMob () {
		return essenceType;
	}

	private int soulInquiryResult = 0;

	public int soulInquiry () {
		return soulInquiryResult;
	}

	public void updateSoulInquiry () {
		soulInquiryResult = 0;

		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) return;

		final AxisAlignedBB activationBox = new AxisAlignedBB(pos).grow(activatingRange);

		for (final EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, activationBox)) {
			if (entity instanceof EntityPlayer) continue;

			final ResourceLocation entityType = EntityList.getKey(entity);
			if (!entityType.toString().equalsIgnoreCase(essenceType))
				continue;

			soulInquiryResult += 1;
		}
	}

	private int timeTillNextMajorUpdate = 0;

	@Override
	public void update () {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(SoulInquirer.HAS_SOULBOOK, false));
			return;
		}

		if (!hasInit) {
			hasInit = true;
			onUpdateUpgrades(false);
		}

		if (timeTillNextMajorUpdate-- < 0) {
			timeTillNextMajorUpdate = 20;
			updateSoulInquiry();
		}

		if (world.isRemote) {
			updateRenderer();

		} else {
			updateSignalStrength();
		}
	}

	private void updateSignalStrength () {
		final int upgradeCount = upgrades.get(Upgrade.COUNT);

		int signalStrength;
		if (upgradeCount == 0) {
			signalStrength = soulInquiryResult > 0 ? 15 : 0;
		} else {
			signalStrength = (int) (Math.min(15, soulInquiryResult / (upgradeCount * 16.0 - 1)) * 16);
		}

		if (signalStrength != this.signalStrength) {
			this.signalStrength = signalStrength;
			markDirty();
		}
	}

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		hasInit = true;

		essenceType = compound.getString("entity_type");
		onUpdateUpgrades(false);
	}

	@Nonnull
	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(SoulInquirer.HAS_SOULBOOK, false));
			return;
		}

		compound.setString("entity_type", essenceType);
	}

	private boolean isPlayerInRangeForEffects () {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 64);
	}

	private void updateRenderer () {
		final int upgradeCount = upgrades.get(Upgrade.COUNT);

		double rotationAmount;
		if (upgradeCount == 0) {
			rotationAmount = soulInquiryResult > 0 ? 1 / 8.0 : 0;
		} else {
			rotationAmount = Math.min(16, soulInquiryResult / (upgradeCount * 16.0 - 1)) / 8;
		}

		double diff = mobRotation - prevMobRotation;
		prevMobRotation = mobRotation;
		mobRotation += soulInquiryResult == 0 ? diff * 0.9 : rotationAmount + diff * 0.8;

		if (isPlayerInRangeForEffects()) {
			double particleCount = CONFIG.particleCount * Math
				.min(1, soulInquiryResult) / 2;
			if (particleCount < 1) {
				timeTillParticle += 0.01 + particleCount;

				if (timeTillParticle < 1)
					return;
			}

			timeTillParticle = 0;

			for (int i = 0; i < particleCount; i++) {
				double d3 = (pos.getX() + world.rand.nextFloat());
				double d4 = (pos.getY() + world.rand.nextFloat());
				double d5 = (pos.getZ() + world.rand.nextFloat());
				world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (d3 - pos.getX() - 0.5F), -0.3D, (d5 - pos
					.getZ() - 0.5F));
			}
		}
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock() || oldState.getValue(SoulInquirer.HAS_SOULBOOK) != newState
			.getValue(SoulInquirer.HAS_SOULBOOK);
	}
}
