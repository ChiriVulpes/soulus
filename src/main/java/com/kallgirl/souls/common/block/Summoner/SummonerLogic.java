package com.kallgirl.souls.common.block.Summoner;

import com.google.common.collect.Lists;
import com.kallgirl.souls.common.ModObjects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public abstract class SummonerLogic {

	public abstract World getSpawnerWorld();

	@Nonnull
	public abstract BlockPos getSpawnerPosition();

	/** The delay to spawn. */
	private int spawnDelay = 20;
	private final List<WeightedSpawnerEntity> minecartToSpawn = Lists.newArrayList();
	private WeightedSpawnerEntity randomEntity = new WeightedSpawnerEntity();
	private double mobRotation;
	private double prevMobRotation;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	/** Cached instance of the entity to render inside the summoner. */
	private Entity cachedEntity;
	private int maxNearbyEntities = 6;
	/** The distance from which a player activates the summoner. */
	private int activatingRangeFromPlayer = 16;
	/** The range coefficient for spawning entities around. */
	private int spawnRange = 4;

	/**
	 * Gets the entity name that should be spawned.
	 */
	private String getEntityNameToSpawn() {
		return randomEntity.getNbt().getString("id");
	}

	@ParametersAreNonnullByDefault
	public void setEntityName(String name) {
		randomEntity.getNbt().setString("id", name);
	}

	@ParametersAreNonnullByDefault
	public void setNextSpawnData(WeightedSpawnerEntity spawnerEntity) {
		randomEntity = spawnerEntity;
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
		return getSpawnerWorld().isAnyPlayerWithinRangeAt(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, activatingRangeFromPlayer);
	}

	public void updateSpawner() {
		if (!isActivated()) {
			prevMobRotation = mobRotation;
		} else {
			BlockPos blockpos = getSpawnerPosition();

			if (getSpawnerWorld().isRemote) {
				double d3 = (blockpos.getX() + getSpawnerWorld().rand.nextFloat());
				double d4 = (blockpos.getY() + getSpawnerWorld().rand.nextFloat());
				double d5 = (blockpos.getZ() + getSpawnerWorld().rand.nextFloat());
				getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
				getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

				if (spawnDelay > 0) {
					--spawnDelay;
				}

				prevMobRotation = mobRotation;
				mobRotation = (mobRotation + (1000.0F / (spawnDelay + 200.0F))) % 360.0D;
			} else {
				if (spawnDelay == -1) {
					resetTimer();
				}

				if (spawnDelay > 0) {
					--spawnDelay;
					return;
				}

				boolean flag = false;

				for (int i = 0; i < spawnCount; ++i) {
					NBTTagCompound nbttagcompound = randomEntity.getNbt();
					NBTTagList nbttaglist = nbttagcompound.getTagList("Pos", 6);
					World world = getSpawnerWorld();
					int j = nbttaglist.tagCount();
					double d0 = j >= 1 ? nbttaglist.getDoubleAt(0) : blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange + 0.5D;
					double d1 = j >= 2 ? nbttaglist.getDoubleAt(1) : (blockpos.getY() + world.rand.nextInt(3) - 1);
					double d2 = j >= 3 ? nbttaglist.getDoubleAt(2) : blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange + 0.5D;
					Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, world, d0, d1, d2, false);

					if (entity == null) {
						return;
					}

					int k = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), (blockpos.getX() + 1), (blockpos.getY() + 1), (blockpos.getZ() + 1))).expandXyz(spawnRange)).size();

					if (k >= maxNearbyEntities) {
						resetTimer();
						return;
					}

					EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
					entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

					if (entityliving == null || net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(entityliving, getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {

						// custom data so we know the mob was spawned by souls
						NBTTagCompound entityData = entity.getEntityData();
						entityData.setByte("souls:spawned-by-souls", (byte)1);

						if (randomEntity.getNbt().getSize() == 1 && randomEntity.getNbt().hasKey("id", 8) && entity instanceof EntityLiving) {
							if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(entityliving, getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ))
								entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
						}

						AnvilChunkLoader.spawnEntity(entity, world);
						world.playEvent(2004, blockpos, 0);

						if (entityliving != null) {
							entityliving.spawnExplosionParticle();
						}

						flag = true;
					}
				}

				if (flag) {
					resetTimer();
				}
			}
		}
	}

	private void resetTimer() {
		if (maxSpawnDelay <= minSpawnDelay) {
			spawnDelay = minSpawnDelay;
		} else {
			int i = maxSpawnDelay - minSpawnDelay;
			spawnDelay = minSpawnDelay + getSpawnerWorld().rand.nextInt(i);
		}

		if (!minecartToSpawn.isEmpty()) {
			setNextSpawnData(WeightedRandom.getRandomItem(getSpawnerWorld().rand, minecartToSpawn));
		}

		broadcastEvent(1);
	}

	public void broadcastEvent(int id) {
		getSpawnerWorld().addBlockEvent(getSpawnerPosition(), (Summoner)ModObjects.get("summoner"), id, 0);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		spawnDelay = nbt.getShort("Delay");
		minecartToSpawn.clear();

		if (nbt.hasKey("SpawnPotentials", 9)) {
			NBTTagList nbttaglist = nbt.getTagList("SpawnPotentials", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				minecartToSpawn.add(new WeightedSpawnerEntity(nbttaglist.getCompoundTagAt(i)));
			}
		}

		NBTTagCompound spawnData = nbt.getCompoundTag("SpawnData");

		if (!spawnData.hasKey("id", 8)) {
			spawnData.setString("id", "Pig");
		}

		setNextSpawnData(new WeightedSpawnerEntity(1, spawnData));

		if (nbt.hasKey("MinSpawnDelay", 99)) {
			minSpawnDelay = nbt.getShort("MinSpawnDelay");
			maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
			spawnCount = nbt.getShort("SpawnCount");
		}

		if (nbt.hasKey("MaxNearbyEntities", 99)) {
			maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
			activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
		}

		if (nbt.hasKey("SpawnRange", 99)) {
			spawnRange = nbt.getShort("SpawnRange");
		}

		if (getSpawnerWorld() != null) {
			cachedEntity = null;
		}
	}

	@Nonnull
	@ParametersAreNonnullByDefault
	public NBTTagCompound writeToNBT(NBTTagCompound p_189530_1_) {
		String s = getEntityNameToSpawn();

		if (StringUtils.isNullOrEmpty(s)) {
			return p_189530_1_;
		} else {
			p_189530_1_.setShort("Delay", (short)spawnDelay);
			p_189530_1_.setShort("MinSpawnDelay", (short)minSpawnDelay);
			p_189530_1_.setShort("MaxSpawnDelay", (short)maxSpawnDelay);
			p_189530_1_.setShort("SpawnCount", (short)spawnCount);
			p_189530_1_.setShort("MaxNearbyEntities", (short)maxNearbyEntities);
			p_189530_1_.setShort("RequiredPlayerRange", (short)activatingRangeFromPlayer);
			p_189530_1_.setShort("SpawnRange", (short)spawnRange);
			p_189530_1_.setTag("SpawnData", randomEntity.getNbt().copy());
			NBTTagList nbttaglist = new NBTTagList();

			if (minecartToSpawn.isEmpty()) {
				nbttaglist.appendTag(randomEntity.toCompoundTag());
			} else {
				for (WeightedSpawnerEntity weightedspawnerentity : minecartToSpawn) {
					nbttaglist.appendTag(weightedspawnerentity.toCompoundTag());
				}
			}

			p_189530_1_.setTag("SpawnPotentials", nbttaglist);
			return p_189530_1_;
		}
	}

	/**
	 * Sets the delay to minDelay if parameter given is 1, else return false.
	 */
	public boolean setDelayToMin(int delay) {
		if (delay == 1 && getSpawnerWorld().isRemote) {
			spawnDelay = minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	@SideOnly (Side.CLIENT)
	public Entity getCachedEntity() {
		if (cachedEntity == null) {
			cachedEntity = AnvilChunkLoader.readWorldEntity(randomEntity.getNbt(), getSpawnerWorld(), false);

			if (randomEntity.getNbt().getSize() == 1 && randomEntity.getNbt().hasKey("id", 8) && cachedEntity instanceof EntityLiving) {
				((EntityLiving)cachedEntity).onInitialSpawn(getSpawnerWorld().getDifficultyForLocation(new BlockPos(cachedEntity)), null);
			}
		}

		return cachedEntity;
	}

	@SideOnly(Side.CLIENT)
	public double getMobRotation() {
		return mobRotation;
	}

	@SideOnly(Side.CLIENT)
	public double getPrevMobRotation() {
		return prevMobRotation;
	}
}
