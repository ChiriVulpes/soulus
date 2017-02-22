package com.kallgirl.souls.common.block.Summoner;

import com.kallgirl.souls.common.Config;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.util.NBTBuilder;
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

	/** The delay to spawn. */
	private int spawnDelay = 20;
	private String mobName;
	private double mobRotation;
	private double prevMobRotation;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	/** Cached instance of the mobName to render inside the summoner. */
	private EntityLiving renderMob;
	private int maxNearbyEntities = 6;
	/** The distance from which a player activates the summoner. */
	private int activatingRangeFromPlayer = 16;
	/** The range coefficient for spawning entities around. */
	private int spawnRange = 4;

	public void setMobName (@Nonnull String mobName) {
		this.mobName = mobName;
	}
	public String getMobName() {
		return mobName;
	}

	private void setNextSpawnData(String entity) {
		this.mobName = entity;
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

	public NBTTagCompound getEntityNbt() {
		if (mobName.equals("none")) {
			getSpawnerWorld().setBlockState(getSpawnerPosition(), ModObjects.getBlock("summonerEmpty").getDefaultState());
		}
		String realMobName = mobName;
		Config.SoulInfo soulInfo = Config.getSoulInfo(mobName);
		if (soulInfo.specialSpawnInfo != null) {
			realMobName = soulInfo.specialSpawnInfo.getEntityName();
		}
		return new NBTBuilder().setString("id", realMobName).nbt;
	}

	public void update () {
		if (!isActivated()) {
			prevMobRotation = mobRotation;
		} else {
			World world = getSpawnerWorld();
			BlockPos blockpos = getSpawnerPosition();

			if (world.isRemote) {
				double d3 = (blockpos.getX() + world.rand.nextFloat());
				double d4 = (blockpos.getY() + world.rand.nextFloat());
				double d5 = (blockpos.getZ() + world.rand.nextFloat());
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

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

				boolean spawnedEntity = false;

				for (int i = 0; i < spawnCount; ++i) {
					NBTTagCompound entityNbt = getEntityNbt();
					double x = blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange + 0.5D;
					double y = (blockpos.getY() + world.rand.nextInt(3) - 1);
					double z = blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange + 0.5D;
					EntityLiving entity = (EntityLiving) AnvilChunkLoader.readWorldEntityPos(entityNbt, world, x, y, z, false);

					if (entity == null) return;

					AxisAlignedBB boundingBox = new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1).expandXyz(spawnRange);

					if (world.getEntitiesWithinAABB(entity.getClass(), boundingBox).size() >= maxNearbyEntities) {
						resetTimer();
						return;
					}

					entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

					Config.SoulInfo soulInfo = Config.getSoulInfo(mobName);
					if (soulInfo.specialSpawnInfo != null) {
						entity.readFromNBT(
							new NBTBuilder(entity.writeToNBT(new NBTTagCompound()))
								.addAll(soulInfo.specialSpawnInfo.getEntityNBT())
								.nbt
						);
					}

					if (ForgeEventFactory.canEntitySpawnSpawner(entity, world, (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {

						// custom data so we know the mob was spawned by souls
						NBTTagCompound entityData = entity.getEntityData();
						entityData.setByte("souls:spawned-by-souls", (byte)1);

						if (!ForgeEventFactory.doSpecialSpawn(entity, world, (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {
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
		if (maxSpawnDelay <= minSpawnDelay) {
			spawnDelay = minSpawnDelay;
		} else {
			int i = maxSpawnDelay - minSpawnDelay;
			spawnDelay = minSpawnDelay + getSpawnerWorld().rand.nextInt(i);
		}
		setNextSpawnData(mobName);

		broadcastEvent(1);
	}

	public void broadcastEvent(int id) {
		getSpawnerWorld().addBlockEvent(getSpawnerPosition(), (Summoner)ModObjects.get("summoner"), id, 0);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		spawnDelay = nbt.getShort("Delay");

		if (!nbt.hasKey("SpawnMob", 8)) {
			System.out.println("Summoner NBT missing mob id");
			mobName = "none";
		} else {
			setNextSpawnData(nbt.getString("SpawnMob"));
		}

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
			renderMob = null;
		}
	}

	@Nonnull
	@ParametersAreNonnullByDefault
	public NBTTagCompound writeToNBT(NBTTagCompound nbtIn) {
		return new NBTBuilder(nbtIn)
			.setShort("Delay", spawnDelay)
			.setShort("MinSpawnDelay", minSpawnDelay)
			.setShort("MaxSpawnDelay", maxSpawnDelay)
			.setShort("SpawnCount", spawnCount)
			.setShort("MaxNearbyEntities", maxNearbyEntities)
			.setShort("RequiredPlayerRange", activatingRangeFromPlayer)
			.setShort("SpawnRange", spawnRange)
			.setString("SpawnMob", mobName, true)
			.nbt;
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
	public EntityLiving getCachedMob () {
		if (renderMob == null) {
			NBTTagCompound entityNbt = getEntityNbt();
			World world = getSpawnerWorld();

			renderMob = (EntityLiving) AnvilChunkLoader.readWorldEntity(entityNbt, world, false);

			if (renderMob == null)
				throw new RuntimeException("Unable to summon mobName " + mobName);

			Config.SoulInfo soulInfo = Config.getSoulInfo(mobName);
			if (soulInfo.specialSpawnInfo != null) {
				renderMob.readFromNBT(
					new NBTBuilder(renderMob.writeToNBT(new NBTTagCompound()))
						.addAll(soulInfo.specialSpawnInfo.getEntityNBT())
						.nbt
				);
			}

			renderMob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(renderMob)), null);
			
			if (soulInfo.specialSpawnInfo != null) {
				soulInfo.specialSpawnInfo.modifyEntity(renderMob);
			}
		}

		return renderMob;
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
