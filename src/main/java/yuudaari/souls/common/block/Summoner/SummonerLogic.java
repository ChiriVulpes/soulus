package yuudaari.souls.common.block.Summoner;

import yuudaari.souls.common.Config;
import yuudaari.souls.common.ModBlocks;
import yuudaari.souls.common.util.NBTHelper;
import yuudaari.souls.common.util.Range;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class SummonerLogic {

	public abstract World getSpawnerWorld();

	@Nonnull
	public abstract BlockPos getSpawnerPosition();

	// things the logic uses/keeps track of
	private String mobName;

	public void setMobName(@Nonnull String mobName) {
		this.mobName = mobName;
	}

	public String getMobName() {
		return this.mobName;
	}

	private double mobRotation;
	private double prevMobRotation;
	private int timeTillSpawn = 0;
	private EntityLiving renderMob;

	// non-customisable properties
	private static int maxNearbyEntities = 6;
	private static int spawnBox = 4;
	private static int maximumSpawnCount = 6;
	private static Range<Integer> nonUpgradedSpawnCount = new Range<>(1, 2);
	private static Range<Integer> nonUpgradedSpawnDelay = new Range<>(200, 800);
	private static int nonUpgradedActivatingRange = 16;

	// customisable properties of a summoner
	public Range<Integer> spawnDelay;
	public Range<Integer> spawnCount;
	public int activatingRange;

	public int upgradeCountDelay = 0;
	public int upgradeCountSpawnCount = 0;
	public int upgradeCountRange = 0;

	private void setNextSpawn(String entity) {
		this.mobName = entity;
		setNextSpawn();
	}

	private void setNextSpawn() {
		World world = getSpawnerWorld();
		if (world != null) {
			BlockPos pos = getSpawnerPosition();
			IBlockState blockState = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, blockState, blockState, 4);
		}
	}

	/**
	 * Returns true if there's a player close enough to this to activate it.
	 */
	private boolean isActivated() {
		BlockPos blockpos = getSpawnerPosition();
		return getSpawnerWorld().isAnyPlayerWithinRangeAt(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
				blockpos.getZ() + 0.5D, this.activatingRange);
	}

	public NBTTagCompound getEntityNbt() {
		if (this.mobName.equals("none")) {
			getSpawnerWorld().setBlockState(getSpawnerPosition(), ModBlocks.SUMMONER_EMPTY.getDefaultState());
		}
		String realMobName = this.mobName;
		Config.SoulInfo soulInfo = Config.getSoulInfo(this.mobName);
		if (soulInfo.specialSpawnInfo != null) {
			realMobName = soulInfo.specialSpawnInfo.getEntityName();
		}
		return new NBTHelper().setString("id", realMobName).nbt;
	}

	public void update() {
		if (!isActivated()) {
			this.prevMobRotation = this.mobRotation;
		} else {
			World world = getSpawnerWorld();
			BlockPos blockpos = getSpawnerPosition();

			if (world.isRemote) {
				double d3 = (blockpos.getX() + world.rand.nextFloat());
				double d4 = (blockpos.getY() + world.rand.nextFloat());
				double d5 = (blockpos.getZ() + world.rand.nextFloat());
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

				if (this.timeTillSpawn > 0) {
					--this.timeTillSpawn;
				}

				this.prevMobRotation = this.mobRotation;
				this.mobRotation = (this.mobRotation + (1000.0F / (this.timeTillSpawn + 200.0F))) % 360.0D;
			} else {
				if (this.timeTillSpawn == -1) {
					resetTimer();
				}

				if (this.timeTillSpawn > 0) {
					--this.timeTillSpawn;
					return;
				}

				boolean spawnedEntity = false;
				int spawnCount = this.spawnCount.get(world.rand);

				for (int i = 0; i < spawnCount; ++i) {
					NBTTagCompound entityNbt = getEntityNbt();
					double x = blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnBox + 0.5D;
					double y = (blockpos.getY() + world.rand.nextInt(3) - 1);
					double z = blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnBox + 0.5D;
					EntityLiving entity = (EntityLiving) AnvilChunkLoader.readWorldEntityPos(entityNbt, world, x, y, z,
							false);

					if (entity == null)
						return;

					AxisAlignedBB boundingBox = new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(),
							blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1).grow(SummonerLogic.spawnBox);

					if (world.getEntitiesWithinAABB(entity.getClass(), boundingBox).size() >= maxNearbyEntities) {
						resetTimer();
						return;
					}

					entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F,
							0.0F);

					Config.SoulInfo soulInfo = Config.getSoulInfo(this.mobName);
					if (soulInfo.specialSpawnInfo != null) {
						entity.readFromNBT(new NBTHelper(entity.writeToNBT(new NBTTagCompound()))
								.addAll(soulInfo.specialSpawnInfo.getEntityNBT()).nbt);
					}

					if (ForgeEventFactory.canEntitySpawnSpawner(entity, world, (float) entity.posX, (float) entity.posY,
							(float) entity.posZ)) {

						// custom data so we know the mob was spawned by souls
						NBTTagCompound entityData = entity.getEntityData();
						entityData.setByte("souls:spawned_by_souls", (byte) 1);

						if (!ForgeEventFactory.doSpecialSpawn(entity, world, (float) entity.posX, (float) entity.posY,
								(float) entity.posZ)) {
							entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);

							if (soulInfo.specialSpawnInfo != null) {
								soulInfo.specialSpawnInfo.modifyEntity(entity);
							}
						}

						AnvilChunkLoader.spawnEntity(entity, world);
						world.playEvent(2004, blockpos, 0);

						entity.spawnExplosionParticle();

						spawnedEntity = true;
					}
				}

				if (spawnedEntity) {
					resetTimer();
				}
			}
		}
	}

	private void resetTimer() {
		this.timeTillSpawn = this.spawnDelay.get(getSpawnerWorld().rand);

		setNextSpawn(this.mobName);
		broadcastEvent(1);
	}

	public void broadcastEvent(int id) {
		getSpawnerWorld().addBlockEvent(getSpawnerPosition(), ModBlocks.SUMMONER, id, 0);
	}

	public void setupSpawnData() {
		this.spawnCount = new Range<>(nonUpgradedSpawnCount.getMin() + this.upgradeCountSpawnCount / 3,
				nonUpgradedSpawnCount.getMax() + Math.min(this.upgradeCountSpawnCount, maximumSpawnCount));
		this.spawnDelay = new Range<>((int) (nonUpgradedSpawnDelay.getMin() / (1F + this.upgradeCountDelay * 0.8F)),
				(int) (nonUpgradedSpawnDelay.getMax() / (1F + this.upgradeCountDelay)));
		this.activatingRange = (int) Math
				.floor(nonUpgradedActivatingRange * Math.ceil(1F + this.upgradeCountRange / 2F));
		System.out.println(this.spawnCount.getMin() + ", " + this.spawnCount.getMax());
		System.out.println(this.spawnDelay.getMin() + ", " + this.spawnDelay.getMax());
		System.out.println(this.activatingRange);
	}

	public void readFromNBT(NBTTagCompound nbtIn) {
		try {
			NBTHelper nbt = new NBTHelper(nbtIn);
			setNextSpawn(nbt.getString("MobName"));
			this.timeTillSpawn = nbt.getShort("Delay");
			if (nbt.hasTag("Upgrades", NBTHelper.Tag.COMPOUND)) {
				NBTTagCompound upgrades = nbt.getTag("Upgrades");
				this.upgradeCountDelay = upgrades.getByte("Delay");
				this.upgradeCountSpawnCount = upgrades.getByte("Count");
				this.upgradeCountRange = upgrades.getByte("Range");
				setupSpawnData();
			}
		} catch (Exception e) {
			System.out.println("Summoner NBT missing required tag(s)");
			this.mobName = "none";
		}

		if (getSpawnerWorld() != null) {
			this.renderMob = null;
		}
	}

	@Nonnull
	@ParametersAreNonnullByDefault
	public NBTTagCompound writeToNBT(NBTTagCompound nbtIn) {
		return new NBTHelper(nbtIn).setShort("Delay", this.timeTillSpawn)
				.setTag("Upgrades",
						new NBTHelper().setByte("Delay", this.upgradeCountDelay)
								.setByte("Count", this.upgradeCountSpawnCount).setByte("Range", this.upgradeCountRange))
				.setString("MobName", this.mobName, true).nbt;
	}

	/**
	 * Sets the delay to minDelay if parameter given is 1, else return false.
	 */
	public boolean setDelayToMin(int delay) {
		if (delay == 1 && getSpawnerWorld().isRemote) {
			this.timeTillSpawn = this.spawnDelay.getMin();
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public EntityLiving getCachedMob() {
		if (this.renderMob == null) {
			NBTTagCompound entityNbt = getEntityNbt();
			World world = getSpawnerWorld();

			this.renderMob = (EntityLiving) AnvilChunkLoader.readWorldEntity(entityNbt, world, false);

			if (this.renderMob == null)
				throw new RuntimeException("Unable to summon mobName " + this.mobName);

			Config.SoulInfo soulInfo = Config.getSoulInfo(this.mobName);
			if (soulInfo.specialSpawnInfo != null) {
				this.renderMob.readFromNBT(new NBTHelper(this.renderMob.writeToNBT(new NBTTagCompound()))
						.addAll(soulInfo.specialSpawnInfo.getEntityNBT()).nbt);
			}

			this.renderMob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(this.renderMob)), null);

			if (soulInfo.specialSpawnInfo != null) {
				soulInfo.specialSpawnInfo.modifyEntity(this.renderMob);
			}
		}

		return this.renderMob;
	}

	@SideOnly(Side.CLIENT)
	public double getMobRotation() {
		return this.mobRotation;
	}

	@SideOnly(Side.CLIENT)
	public double getPrevMobRotation() {
		return this.prevMobRotation;
	}
}
