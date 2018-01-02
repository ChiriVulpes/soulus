package yuudaari.soulus.common.block.summoner;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import yuudaari.soulus.common.block.summoner.Summoner.Upgrade;
import yuudaari.soulus.common.block.UpgradeableBlock.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.EssenceConfig;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.Soulus;

public class SummonerTileEntity extends UpgradeableBlockTileEntity implements ITickable {

	@Override
	public Summoner getBlock() {
		return Summoner.INSTANCE;
	}

	/* OTHER */
	private boolean hasInit = false;
	private String essenceType;
	private float timeTillSpawn = 0;
	private float lastTimeTillSpawn;
	public int soulbookUses = getBlock().soulbookUses;

	private int spawningRadius;
	private int activatingRange;
	private Range spawnDelay;
	private Range spawnCount;

	private int signalStrength;

	/* RENDERER */
	private double timeTillParticle = 0;
	public double mobRotation;
	public double prevMobRotation;
	public EntityLiving renderMob;

	public void reset() {
		this.renderMob = null;
		this.resetTimer();
	}

	@Override
	public void onUpdateUpgrades() {
		updateUpgrades(true);
	}

	private void updateUpgrades(boolean resetTimer) {

		Summoner block = getBlock();

		int countUpgrades = upgrades.get(Upgrade.COUNT);
		spawnCount = new Range(block.nonUpgradedCount.min + countUpgrades * block.upgradeCountEffectiveness.min,
				block.nonUpgradedCount.max + countUpgrades * block.upgradeCountEffectiveness.max);
		spawningRadius = (int) Math
				.floor(block.nonUpgradedSpawningRadius + countUpgrades * block.upgradeCountRadiusEffectiveness);

		int delayUpgrades = upgrades.get(Upgrade.DELAY);
		spawnDelay = new Range(block.nonUpgradedDelay.min / (1 + delayUpgrades * block.upgradeDelayEffectiveness.min),
				block.nonUpgradedDelay.max / (1 + delayUpgrades * block.upgradeDelayEffectiveness.max));

		int rangeUpgrades = upgrades.get(Upgrade.RANGE);
		activatingRange = block.nonUpgradedRange + rangeUpgrades * block.upgradeRangeEffectiveness;

		if (world != null && !world.isRemote) {
			if (resetTimer)
				resetTimer(false);
			blockUpdate();
		}

	}

	private EssenceConfig spawnMobConfig;
	private int spawnMobChanceTotal;
	public String lastRenderedEssenceType;

	public String getEssenceType() {
		return essenceType;
	}

	public void setEssenceType(String essenceType) {
		this.essenceType = essenceType;
		this.soulbookUses = Soulus.config.getSoulbookQuantity(essenceType);
		resetEssenceType();
	}

	private void resetEssenceType() {
		for (EssenceConfig config : Soulus.config.essences) {
			if (config.essence.equals(essenceType)) {
				spawnMobConfig = config;

				spawnMobChanceTotal = 0;
				for (double dropChance : config.spawns.values()) {
					spawnMobChanceTotal += dropChance;
				}

				break;
			}
		}
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public NBTTagCompound getEntityNbt() {
		NBTTagCompound result = new NBTTagCompound();
		result.setString("id", getSpawnMob());
		result.setByte("PersistenceRequired", (byte) 1);
		return result;
	}

	private String getSpawnMob() {
		if (spawnMobChanceTotal > 0) {
			int choice = new Random().nextInt(spawnMobChanceTotal);
			for (Map.Entry<String, Double> spawnConfig : spawnMobConfig.spawns.entrySet()) {
				choice -= spawnConfig.getValue();
				if (choice < 0) {
					return spawnConfig.getKey();
				}
			}
		}
		return essenceType;
	}

	public float getSpawnPercent() {
		return (lastTimeTillSpawn - timeTillSpawn) / (float) lastTimeTillSpawn;
	}

	private double activationAmount() {
		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) {
			return 0;
		}

		// when the soulbook is empty, don't run
		if (getBlock().soulbookUses > 0 && soulbookUses <= 0) {
			return 0;
		}

		double activationAmount = 0;

		for (EntityPlayer player : world.playerEntities) {

			if (EntitySelectors.NOT_SPECTATING.apply(player)) {
				double d0 = player.getDistanceSqToCenter(pos);

				double nearAmt = (d0 / (activatingRange * activatingRange));
				activationAmount += Math.max(0, (1 - (nearAmt * nearAmt)) * 2);
			}
		}

		return activationAmount;
	}

	@Override
	public void update() {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(Summoner.HAS_SOULBOOK, false));
			return;
		}

		if (!hasInit) {
			hasInit = true;
			onUpdateUpgrades();
		}

		double activationAmount = activationAmount();
		if (activationAmount <= 0) {
			// ease rotation to a stop
			double diff = mobRotation - prevMobRotation;
			prevMobRotation = mobRotation;
			mobRotation = mobRotation + diff * 0.9;
			return;
		}

		if (timeTillSpawn > 0) {
			timeTillSpawn -= activationAmount;

			if (world.isRemote) {
				updateRenderer();
			} else {
				int signalStrength = (int) Math.floor(16 * getSpawnPercent());
				if (signalStrength != this.signalStrength) {
					this.signalStrength = signalStrength;
					markDirty();
				}
			}

			return;
		}

		if (!world.isRemote)
			spawn();

		resetTimer();
	}

	private void resetTimer() {
		resetTimer(true);
	}

	private void resetTimer(boolean update) {
		timeTillSpawn = spawnDelay.get(world.rand).intValue();
		lastTimeTillSpawn = timeTillSpawn;

		if (update)
			blockUpdate();
	}

	@Override
	public void onReadFromNBT(NBTTagCompound compound) {
		hasInit = true;

		essenceType = compound.getString("entity_type");
		soulbookUses = compound.getInteger("soulbook_uses");
		resetEssenceType();
		timeTillSpawn = compound.getFloat("delay");
		lastTimeTillSpawn = compound.getFloat("delay_last");

		updateUpgrades(false);
	}

	@Nonnull
	@Override
	public void onWriteToNBT(NBTTagCompound compound) {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(Summoner.HAS_SOULBOOK, false));
			return;
		}

		compound.setString("entity_type", essenceType);
		compound.setInteger("soulbook_uses", soulbookUses);
		compound.setFloat("delay", timeTillSpawn);
		compound.setFloat("delay_last", lastTimeTillSpawn);
	}

	private boolean isPlayerInRangeForEffects() {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 64);
	}

	private void updateRenderer() {
		Summoner block = getBlock();

		if (isPlayerInRangeForEffects()) {
			if (block.particleCountActivated < 1) {
				timeTillParticle += block.particleCountActivated;

				if (timeTillParticle < 1)
					return;
			}

			timeTillParticle = 0;

			for (int i = 0; i < block.particleCountActivated; i++) {
				double d3 = (pos.getX() + world.rand.nextFloat());
				double d4 = (pos.getY() + world.rand.nextFloat());
				double d5 = (pos.getZ() + world.rand.nextFloat());
				world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (d3 - pos.getX() - 0.5F), -0.3D,
						(d5 - pos.getZ() - 0.5F));
			}

			double diff = mobRotation - prevMobRotation;
			prevMobRotation = mobRotation;
			mobRotation = mobRotation + 1.0F * getSpawnPercent() + diff * 0.8;
		}
	}

	private int spawn() {

		int spawnCount = this.spawnCount.get(world.rand).intValue();
		int spawned = 0;

		MainSpawningLoop: for (int i = 0; i < spawnCount; i++) {
			NBTTagCompound entityNbt = getEntityNbt();
			for (int tries = 0; tries < 5; tries++) {
				double x = pos.getX() + world.rand.nextDouble() * spawningRadius * 2 + 0.5D - spawningRadius;
				double y = pos.getY() + world.rand.nextDouble() * spawningRadius / 2 + 0.5D - spawningRadius / 4;
				double z = pos.getZ() + world.rand.nextDouble() * spawningRadius * 2 + 0.5D - spawningRadius;
				EntityLiving entity = (EntityLiving) AnvilChunkLoader.readWorldEntityPos(entityNbt, world, x, y, z,
						false);

				if (entity == null) {
					return 0;
				}

				AxisAlignedBB boundingBox = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1,
						pos.getY() + 1, pos.getZ() + 1).grow(spawningRadius);

				// check if there's too many entities in the way
				if (world.getEntitiesWithinAABB(entity.getClass(), boundingBox)
						.size() >= Math.pow(spawningRadius * 2, 2) / 10) {
					// we can return here because this check won't change next loop
					break MainSpawningLoop;
				}

				// check if there's blocks in the way
				if (world.collidesWithAnyBlock(entity.getEntityBoundingBox())) {
					// we chose a bad position, so we continue and try again
					continue;
				}

				entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F,
						0.0F);

				// custom data so we know the mob was spawned by souls
				NBTTagCompound entityData = entity.getEntityData();
				entityData.setByte("soulus:spawn_whitelisted", (byte) 2);

				if (!ForgeEventFactory.doSpecialSpawn(entity, world, (float) entity.posX, (float) entity.posY,
						(float) entity.posZ)) {
					entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
				}

				AnvilChunkLoader.spawnEntity(entity, world);

				if (isPlayerInRangeForEffects())
					explosionParticles(entity);
				world.setEntityState(entity, (byte) 20);

				spawned++;

				// we successfully spawned, so exit the try-to-spawn loop
				break;
			}
		}

		soulbookUses -= spawned;

		return spawned;
	}

	private void explosionParticles(EntityLiving entity) {
		Random rand = world.rand;

		WorldServer worldServer = world.getMinecraftServer().getWorld(entity.dimension);

		for (int i = 0; i < getBlock().particleCountSpawn; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH,
					entity.posX + (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width
							- d0 * 10.0D,
					entity.posY + (double) (rand.nextFloat() * entity.height) - d1 * 10.0D, entity.posZ
							+ (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width - d2 * 10.0D,
					1, d0, d1, d2, Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2));

			double d3 = (pos.getX() + rand.nextFloat());
			double d4 = (pos.getY() + rand.nextFloat());
			double d5 = (pos.getZ() + rand.nextFloat());
			double d3o = (pos.getX() - d3 - 0.5F) / 20;
			double d4o = (pos.getY() - d4) / 20;
			double d5o = (pos.getZ() - d5 - 0.5F) / 20;
			worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH, d3, d4, d5, 1, d3o, d4o, d5o,
					Math.sqrt(d3o * d3o + d4o * d4o + d5o * d5o) * 2);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock()
				|| oldState.getValue(Summoner.HAS_SOULBOOK) != newState.getValue(Summoner.HAS_SOULBOOK);
	}
}