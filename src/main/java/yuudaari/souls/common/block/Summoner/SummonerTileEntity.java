package yuudaari.souls.common.block.Summoner;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import yuudaari.souls.common.ModBlocks;
import yuudaari.souls.common.util.NBTHelper;
import yuudaari.souls.common.util.Range;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SummonerTileEntity extends TileEntity implements ITickable {

	public static enum Upgrade {
		COUNT, DELAY, RANGE
	}

	/* CONFIGS */
	private static int nonUpgradedSpawningRadius = 4;
	private static Range<Integer> nonUpgradedCount = new Range<>(1, 2);
	private static Range<Integer> nonUpgradedDelay = new Range<>(10000, 20000);
	private static int nonUpgradedRange = 4;
	private static Map<Upgrade, Integer> maxUpgrades = new HashMap<>();
	static {
		maxUpgrades.put(Upgrade.COUNT, 64);
		maxUpgrades.put(Upgrade.DELAY, 64);
		maxUpgrades.put(Upgrade.RANGE, 64);
	}

	/* OTHER */
	private boolean hasInit = false;
	private String spawnMob;
	private int timeTillSpawn = 0;
	private int lastTimeTillSpawn;
	private Map<Upgrade, Integer> upgradeCounts = new HashMap<>();
	{
		upgradeCounts.put(Upgrade.COUNT, 0);
		upgradeCounts.put(Upgrade.DELAY, 0);
		upgradeCounts.put(Upgrade.RANGE, 0);
	}

	private int spawningRadius;
	private int activatingRange;
	private Range<Integer> spawnDelay;
	private Range<Integer> spawnCount;

	private int signalStrength;

	/* RENDERER */
	public double mobRotation;
	public double prevMobRotation;
	public EntityLiving renderMob;

	public boolean addUpgrade(Upgrade upgradeType) {
		boolean result = addUpgradeStack(upgradeType, 1) == 1;
		updateUpgrades();
		return result;
	}

	public int addUpgradeStack(Upgrade upgradeType, int count) {
		int oldCount = upgradeCounts.get(upgradeType);
		int maximum = maxUpgrades.get(upgradeType);
		int newCount = oldCount + count;
		if (newCount > maximum)
			newCount = maximum;
		upgradeCounts.put(upgradeType, newCount);
		updateUpgrades();
		return newCount - oldCount;
	}

	private void setUpgradeCount(Upgrade upgradeType, int newCount) {
		int maximum = maxUpgrades.get(upgradeType);
		if (newCount > maximum)
			newCount = maximum;
		upgradeCounts.put(upgradeType, newCount);
	}

	private void updateUpgrades() {
		updateUpgrades(true);
	}

	private void updateUpgrades(boolean resetTimer) {
		int countUpgrades = upgradeCounts.get(Upgrade.COUNT);
		spawnCount = new Range<>(nonUpgradedCount.getMin() + countUpgrades / 3,
				nonUpgradedCount.getMax() + countUpgrades);
		spawningRadius = nonUpgradedSpawningRadius + countUpgrades / 6;

		int delayUpgrades = upgradeCounts.get(Upgrade.DELAY);
		spawnDelay = new Range<>((int) (nonUpgradedDelay.getMin() / (1F + delayUpgrades * 0.8F)),
				(int) (nonUpgradedDelay.getMax() / (1F + delayUpgrades)));

		int rangeUpgrades = upgradeCounts.get(Upgrade.RANGE);
		activatingRange = nonUpgradedRange + nonUpgradedRange * rangeUpgrades;

		if (world != null && !world.isRemote) {
			if (resetTimer)
				resetTimer(false);
			blockUpdate();
		}
	}

	public String getMob() {
		return spawnMob;
	}

	public void setMob(String mobName) {
		spawnMob = mobName;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public NBTTagCompound getEntityNbt() {
		return new NBTHelper().setString("id", spawnMob).nbt;
	}

	private float getSpawnPercent() {
		return (lastTimeTillSpawn - timeTillSpawn) / (float) lastTimeTillSpawn;
	}

	/** Whether there's a player close enough to activate it */
	private boolean isActivated() {
		return world.isAnyPlayerWithinRangeAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, activatingRange)
				&& world.isBlockIndirectlyGettingPowered(pos) == 0;
	}

	@Override
	public void update() {
		if (spawnMob == null) {
			world.setBlockState(pos, ModBlocks.SUMMONER_EMPTY.getDefaultState());
			return;
		}

		if (!hasInit) {
			hasInit = true;
			updateUpgrades();
		}

		if (!isActivated()) {
			// ease rotation to a stop
			double diff = mobRotation - prevMobRotation;
			prevMobRotation = mobRotation;
			mobRotation = mobRotation + diff * 0.9;
			return;
		}

		if (timeTillSpawn > 0) {
			timeTillSpawn--;

			if (world.isRemote) {
				updateRenderer();
			} else {
				int signalStrength = (int) Math.floor(16 * getSpawnPercent());
				if (signalStrength != this.signalStrength) {
					this.signalStrength = signalStrength;
					this.markDirty();
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
		timeTillSpawn = spawnDelay.get(world.rand);
		lastTimeTillSpawn = timeTillSpawn;

		if (update)
			blockUpdate();
	}

	private void blockUpdate() {
		if (world != null) {
			IBlockState blockState = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, blockState, blockState, 7);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		hasInit = true;

		super.readFromNBT(compound);

		spawnMob = compound.getString("entity_type");
		timeTillSpawn = compound.getInteger("delay");
		lastTimeTillSpawn = compound.getInteger("delay_last");
		NBTTagCompound upgradeTag = compound.getCompoundTag("upgrades");
		setUpgradeCount(Upgrade.COUNT, upgradeTag.getByte("count"));
		setUpgradeCount(Upgrade.DELAY, upgradeTag.getByte("delay"));
		setUpgradeCount(Upgrade.RANGE, upgradeTag.getByte("range"));
		updateUpgrades(false);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (spawnMob == null) {
			world.setBlockState(pos, ModBlocks.SUMMONER_EMPTY.getDefaultState());
			return compound;
		}

		super.writeToNBT(compound);
		return new NBTHelper(compound).setString("entity_type", spawnMob).setInteger("delay", timeTillSpawn)
				.setInteger("delay_last", lastTimeTillSpawn).setTag("upgrades",
						new NBTHelper().setByte("count", upgradeCounts.get(Upgrade.COUNT))
								.setByte("delay", upgradeCounts.get(Upgrade.DELAY))
								.setByte("range", upgradeCounts.get(Upgrade.RANGE)).nbt).nbt;
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
		return nbt;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	private boolean isPlayerInRangeForEffects() {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 64);
	}

	private void updateRenderer() {
		if (isPlayerInRangeForEffects()) {
			for (int i = 0; i < 3; i++) {
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

		int spawnCount = this.spawnCount.get(world.rand);
		int spawned = 0;

		for (int i = 0; i < spawnCount; i++) {
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
					return spawned;
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
				entityData.setByte("souls:spawned_by_souls", (byte) 1);

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

		return spawned;
	}

	private void explosionParticles(EntityLiving entity) {
		Random rand = world.rand;

		// this line will crash if this is ever called from the client
		WorldServer worldServer = world.getMinecraftServer().getWorld(entity.dimension);

		for (int i = 0; i < 50; ++i) {
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
}