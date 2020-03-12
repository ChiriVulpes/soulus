package yuudaari.soulus.common.block.summoner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import scala.Tuple2;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.block.soul_totem.SoulTotemTileEntity;
import yuudaari.soulus.common.block.summoner.Summoner.Upgrade;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSummoner;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.misc.ConfigDespawn;
import yuudaari.soulus.common.item.EssencePerfect.EssenceAlignment;
import yuudaari.soulus.common.misc.SpawnType;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.Range;

@ConfigInjected(Soulus.MODID)
public class SummonerTileEntity extends UpgradeableBlockTileEntity implements ITickable {

	@Inject public static ConfigSummoner CONFIG;
	@Inject public static ConfigEssences CONFIG_ESSENCES;
	@Inject public static ConfigDespawn CONFIG_DESPAWN;

	@Override
	public Summoner getBlock () {
		return BlockRegistry.SUMMONER;
	}

	/* OTHER */
	private boolean hasInit = false;
	private String essenceType;
	private float timeTillSpawn = 0;
	private float lastTimeTillSpawn;
	private float boost = 0;
	private Float soulbookUses = null;
	private boolean malice = true;

	public boolean hasMalice () {
		return malice && upgrades.get(Upgrade.CRYSTAL_DARK) > 0;
	}

	public float getSoulbookUses () {
		if (soulbookUses == null && CONFIG.soulbookUses != null && CONFIG.soulbookUses > 0)
			setSoulbookUses((float) 1);
		return soulbookUses;
	}

	public void setSoulbookUses (Float val) {
		soulbookUses = val == null ? null : Math.max(val, CONFIG.soulbookUses) * (float) CONFIG_ESSENCES.getSoulbookUsesMultiplier(essenceType);
	}

	public double getMaxSoulbookUses () {
		return CONFIG.soulbookUses * CONFIG_ESSENCES.getSoulbookUsesMultiplier(essenceType);
	}

	private int spawningRadius;
	private int activatingRange;
	private Range spawnDelay;
	private Range spawnCount;
	private boolean usedPlayer = false;
	private boolean hadInitialSpawn = false;

	private int signalStrength;

	/* RENDERER */
	private double timeTillParticle = 0;
	public double mobRotation;
	public double prevMobRotation;
	public EntityLiving renderMob;

	public void reset () {
		this.renderMob = null;
		this.resetTimer();
	}

	@Override
	public void onInsertUpgrade (ItemStack stack, IUpgrade upgrade, int newQuantity) {
		if (upgrade == Upgrade.CRYSTAL_DARK) malice = false;
	}

	private void onUpdateUpgrades () {
		onUpdateUpgrades(false);
	}

	private boolean setSpawnCount (final Range count) {
		if (count.equals(spawnCount)) return false;
		spawnCount = count;
		return true;
	}

	private boolean setSpawningRadius (final int radius) {
		if (spawningRadius == radius) return false;
		spawningRadius = radius;
		return true;
	}

	private boolean setSpawnDelay (final Range delay) {
		if (delay.equals(spawnDelay)) return false;
		spawnDelay = delay;
		return true;
	}

	private boolean setActivatingRange (final int range) {
		if (activatingRange == range) return false;
		activatingRange = range;
		return true;
	}

	@Override
	public void onUpdateUpgrades (final boolean readFromNBT) {
		if (isInvalid()) {
			Soulus.removeConfigReloadHandler(this::onUpdateUpgrades);
			return;
		}

		Soulus.onConfigReload(this::onUpdateUpgrades);

		boolean changed = false;

		if (upgrades.get(Upgrade.CRYSTAL_DARK) > 0) {
			// dark crystal
			changed = setSpawnCount(CONFIG.midnightJewelCount) || changed;
			changed = setSpawningRadius(CONFIG.midnightJewelSpawningRadius) || changed;
			changed = setSpawnDelay(CONFIG.midnightJewelDelay) || changed;
			changed = setActivatingRange(CONFIG.midnightJewelRange) || changed;

		} else {
			// normal upgrades
			final int countUpgrades = upgrades.get(Upgrade.COUNT);
			final Range newSpawnCount = new Range(CONFIG.nonUpgradedCount.min + countUpgrades * CONFIG.upgradeCountEffectiveness.min, CONFIG.nonUpgradedCount.max + countUpgrades * CONFIG.upgradeCountEffectiveness.max);
			changed = setSpawnCount(newSpawnCount) || changed;
			final int newSpawningRadius = (int) Math.floor(CONFIG.nonUpgradedSpawningRadius + countUpgrades * CONFIG.upgradeCountRadiusEffectiveness);
			changed = setSpawningRadius(newSpawningRadius) || changed;

			int delayUpgrades = upgrades.get(Upgrade.DELAY);
			final Range newSpawnDelay = new Range(CONFIG.nonUpgradedDelay.min / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.min), CONFIG.nonUpgradedDelay.max / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.max));
			changed = setSpawnDelay(newSpawnDelay) || changed;

			final int rangeUpgrades = upgrades.get(Upgrade.RANGE);
			final int newActivatingRange = CONFIG.nonUpgradedRange + rangeUpgrades * CONFIG.upgradeRangeEffectiveness;
			changed = setActivatingRange(newActivatingRange) || changed;
		}


		if (world != null && !world.isRemote) {
			if (!readFromNBT && changed)
				resetTimer(false);
			blockUpdate();
		}

	}

	private ConfigEssence spawnMobConfig;
	private double spawnMobChanceTotal;
	public String lastRenderedEssenceType;

	public String getEssenceType () {
		return essenceType;
	}

	public void setEssenceType (String essenceType) {
		this.essenceType = essenceType;
		resetEssenceType();
	}

	private void resetEssenceType () {
		spawnMobConfig = CONFIG_ESSENCES.get(essenceType);

		final Map<String, Double> spawnChances = spawnMobConfig == null ? null //
			: hasMalice() && spawnMobConfig.spawnsMalice != null ? spawnMobConfig.spawnsMalice : spawnMobConfig.spawns;
		spawnMobChanceTotal = spawnChances == null ? 0 //
			: (int) (double) spawnChances.values().stream().collect(Collectors.summingDouble(Double::doubleValue));
	}

	public int getSignalStrength () {
		return signalStrength;
	}

	public NBTTagCompound getEntityNbt () {
		NBTTagCompound result = new NBTTagCompound();
		result.setString("id", getSpawnMob());
		return result;
	}

	private String getSpawnMob () {
		if (spawnMobChanceTotal <= 0 || spawnMobConfig == null)
			return essenceType;

		final AtomicDouble choice = new AtomicDouble(new Random().nextDouble() * spawnMobChanceTotal);
		final Map<String, Double> spawnChances = hasMalice() && spawnMobConfig.spawnsMalice != null ? spawnMobConfig.spawnsMalice : spawnMobConfig.spawns;
		return spawnChances == null ? essenceType : spawnChances.entrySet()
			.stream()
			.filter(spawnConfig -> choice.addAndGet((int) -spawnConfig.getValue()) < 0)
			.findFirst()
			.map(Map.Entry::getKey)
			.orElse(essenceType);
	}

	/**
	 * Returns a number between 0 and 1 representing the % summoned
	 */
	public float getSpawnPercent () {
		return (lastTimeTillSpawn - timeTillSpawn) / (float) lastTimeTillSpawn;
	}

	private double activationAmount = 0;
	private Set<EntityPlayer> players = new HashSet<>();

	private void updateActivationAmount () {
		activationAmount = 0;

		// when powered by redstone and player created, don't run
		if ((!malice || upgrades.get(Upgrade.CRYSTAL_DARK) < 1) && world.isBlockIndirectlyGettingPowered(pos) != 0)
			return;

		// when the soulbook is empty, don't run
		if (CONFIG.soulbookUses != null && CONFIG.soulbookUses > 0 && getSoulbookUses() <= 0)
			return;

		if (!hasMalice()) players.clear();

		for (EntityPlayer player : world.playerEntities) {
			if (!EntitySelectors.NOT_SPECTATING.apply(player)) continue;

			double d0 = player.getDistanceSqToCenter(pos);

			double nearAmt = (d0 / (activatingRange * activatingRange));
			activationAmount += Math.max(0, (1 - (nearAmt * nearAmt)) * 2);

			if (activationAmount > 0) {
				if (!hasMalice()) players.add(player);
				usedPlayer = true;
			}
		}

		for (TileEntity te : world.loadedTileEntityList) {
			if (!(te instanceof SoulTotemTileEntity) || !((SoulTotemTileEntity) te).isActive()) continue;

			if (!hasMalice()) {
				SoulTotemTileEntity ste = (SoulTotemTileEntity) te;
				EntityPlayer player = ste.getOwner();
				if (player != null) players.add(player);
			}

			BlockPos tePos = te.getPos();
			double d0 = pos.distanceSqToCenter(tePos.getX() + 0.5, tePos.getY() + 0.5, tePos.getZ() + 0.5);

			double nearAmt = (d0 / (activatingRange * activatingRange));
			activationAmount += Math.max(0, (1 - (nearAmt * nearAmt)) * 2);
		}

		if (boost > 0.01) {
			activationAmount *= 1 + boost;
			boost *= CONFIG.perfectEssenceBoostDecay;
		}
	}

	private int timeTillNextMajorUpdate = 0;

	public boolean insertPerfectEssence (final ItemStack stack, final EntityPlayer player) {
		final String essenceType = getEssenceType();
		final EssenceAlignment alignment = new EssenceAlignment(stack);
		final String[] perfectEssenceTypes = alignment.getEssenceTypes().toArray(String[]::new);

		if (!Arrays.stream(perfectEssenceTypes).anyMatch(essenceType::equalsIgnoreCase))
			return false;

		final int count = player.isSneaking() ? stack.getCount() : 1;

		for (int i = 0; i < count; i++)
			boost += (CONFIG.perfectEssenceBoostBase + CONFIG.perfectEssenceBoostMultiplier * perfectEssenceTypes.length * alignment.getAlignment(essenceType)) //
				* spawnDelay.get(world.rand);

		blockUpdate();
		timeTillNextMajorUpdate = 0;

		if (!player.isCreative()) stack.shrink(count);
		return true;
	}

	@Override
	public void update () {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(Summoner.HAS_SOULBOOK, false));
			return;
		}

		if (!hasInit) {
			hasInit = true;
			onUpdateUpgrades(false);
		}

		if (--timeTillNextMajorUpdate < 0) {
			timeTillNextMajorUpdate = 20;
			updateActivationAmount();
		}

		if (world.isRemote) {
			updateRenderer(activationAmount);

		} else {
			updateSignalStrength(activationAmount);
		}

		timeTillSpawn -= activationAmount;

		if (timeTillSpawn <= 0 || (!hadInitialSpawn && hasMalice() && isPlayerInRange(8))) {
			resetTimer();
			if (!world.isRemote)
				spawn();
		}
	}

	private void updateSignalStrength (double activationAmount) {
		int signalStrength = activationAmount <= 0 ? 0 : (int) Math.floor(15 * getSpawnPercent()) + 1;
		if (signalStrength != this.signalStrength) {
			this.signalStrength = signalStrength;
			markDirty();
		}
	}

	private void resetTimer () {
		resetTimer(true);
	}

	private void resetTimer (boolean update) {
		timeTillSpawn = spawnDelay.getInt(world.rand);
		lastTimeTillSpawn = timeTillSpawn;
		timeTillNextMajorUpdate = 0;

		if (update)
			blockUpdate();
	}

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		hasInit = true;

		essenceType = compound.getString("entity_type");
		soulbookUses = compound.getFloat("soulbook_uses");
		if (soulbookUses == -101 || CONFIG.soulbookUses == null || CONFIG.soulbookUses <= 0)
			soulbookUses = null;
		malice = compound.getBoolean("malice");
		resetEssenceType();
		timeTillSpawn = compound.getFloat("delay");
		lastTimeTillSpawn = compound.getFloat("delay_last");
		hadInitialSpawn = compound.getBoolean("had_initial_spawn");
		usedPlayer = compound.getBoolean("used_player");
		boost = compound.getFloat("boost");

		onUpdateUpgrades(false);

		timeTillNextMajorUpdate = 0;
	}

	@Nonnull
	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		if (essenceType == null) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(Summoner.HAS_SOULBOOK, false));
			return;
		}

		compound.setString("entity_type", essenceType);
		compound.setFloat("soulbook_uses", soulbookUses == null ? -101 : soulbookUses);
		compound.setFloat("delay", timeTillSpawn);
		compound.setFloat("delay_last", lastTimeTillSpawn);
		compound.setBoolean("malice", malice);
		compound.setBoolean("had_initial_spawn", hadInitialSpawn);
		compound.setBoolean("used_player", usedPlayer);
		compound.setFloat("boost", boost);
	}

	private boolean isPlayerInRange (int range) {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), range);
	}

	private void updateRenderer (double activationAmount) {
		double diff = mobRotation - prevMobRotation;
		prevMobRotation = mobRotation;
		mobRotation += activationAmount <= 0 ? diff * 0.9 : getSpawnPercent() + diff * 0.8;

		if (activationAmount <= 0) return;

		if (isPlayerInRange(64)) {
			double particleCount = hasMalice() ? CONFIG.particleCountMidnightJewel : CONFIG.particleCountActivated;
			particleCount *= Math.min(1, activationAmount * activationAmount) * (0.5 + getSpawnPercent() / 2);
			if (particleCount < 1) {
				timeTillParticle += 0.01 + particleCount;
				if (timeTillParticle < 1)
					return;
			}

			timeTillParticle = 0;

			for (int i = 0; i < particleCount; i++) {
				if (hasMalice()) {
					double ox = world.rand.nextFloat();
					double oy = world.rand.nextFloat();
					double oz = world.rand.nextFloat();
					world.spawnParticle(ParticleType.CRYSTAL_DARK.getId(), false, //
						pos.getX() + ox, pos.getY() + 0.7 + oy * 0.5, pos.getZ() + oz, //
						(ox - 0.5) * 0.05, -0.3D, (oz - 0.5) * 0.05);

				} else {
					double d3 = (pos.getX() + world.rand.nextFloat());
					double d4 = (pos.getY() + world.rand.nextFloat());
					double d5 = (pos.getZ() + world.rand.nextFloat());
					world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (d3 - pos.getX() - 0.5F), -0.3D, (d5 - pos
						.getZ() - 0.5F));
				}
			}
		}
	}

	private int spawn () {

		int spawnCount = this.spawnCount.getInt(world.rand);
		int spawned = 0;

		MainSpawningLoop:
		for (int i = 0; i < spawnCount; i++) {
			NBTTagCompound entityNbt = getEntityNbt();
			for (int tries = 0; tries < 5; tries++) {
				double x = pos.getX() + world.rand.nextDouble() * spawningRadius * 2 + 0.5D - spawningRadius;
				double y = pos.getY() + world.rand.nextDouble() * spawningRadius / 2 + 0.5D - spawningRadius / 4;
				double z = pos.getZ() + world.rand.nextDouble() * spawningRadius * 2 + 0.5D - spawningRadius;
				EntityLiving entity = (EntityLiving) AnvilChunkLoader
					.readWorldEntityPos(entityNbt, world, x, y, z, false);

				if (entity == null) {
					return 0;
				}

				AxisAlignedBB boundingBox = new AxisAlignedBB(pos.getX(), pos.getY(), pos
					.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(spawningRadius);

				// check if there's too many entities in the spawning area
				if (world.getEntitiesWithinAABB(entity.getClass(), boundingBox).size() >= Math.pow(spawningRadius * 2, 2) / 8) {
					// we can return here because this check won't change next loop
					break MainSpawningLoop;
				}

				// check if there's blocks in the way
				if (world.collidesWithAnyBlock(entity.getEntityBoundingBox())) {
					// we chose a bad position, so we continue and try again
					continue;
				}

				// custom data so we know the mob was spawned by souls
				final SpawnType spawnType = hasMalice() ? SpawnType.SUMMONED_MALICE : SpawnType.SUMMONED;
				spawnType.apply(entity);
				if (!CONFIG_DESPAWN.despawnMobsSummoned)
					entity.enablePersistence();

				entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

				final ModPotionEffect[] potionEffects = CONFIG.stylePotionEffects
					.get(world.getBlockState(pos).getValue(Summoner.VARIANT));

				if (potionEffects != null)
					for (final ModPotionEffect effect : potionEffects)
					effect.apply(entity);

				final LivingSpawnEvent.SpecialSpawn specialSpawn = new LivingSpawnEvent.SpecialSpawn(entity, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ, null);
				if (!MinecraftForge.EVENT_BUS.post(specialSpawn))
					entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);

				AnvilChunkLoader.spawnEntity(entity, world);

				final ConfigEssence essenceConfig = CONFIG_ESSENCES.get(essenceType);
				if (essenceConfig == null)
					Logger.warn("Tried to summon an invalid essence type " + essenceType);

				final String[] spawnNames = essenceConfig == null ? null : essenceConfig.spawnNames;
				if (spawnNames != null && spawnNames.length > 0)
					entity.setCustomNameTag(spawnNames[world.rand.nextInt(spawnNames.length)]);

				if (isPlayerInRange(64))
					explosionParticles(entity);

				spawned++;

				// Logger.info("spawned " + essenceType + " by " + players.stream().map(player -> player.getName()).collect(Collectors.joining(", ")));

				for (EntityPlayer player : players) {
					Advancements.SUMMON_CREATURE.trigger(player, new Tuple2<>(essenceType, usedPlayer));
				}

				world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 0.5F, world.rand
					.nextFloat() * 0.25F + 0.6F);

				// we successfully spawned, so exit the try-to-spawn loop
				break;
			}
		}

		if (CONFIG.soulbookUses != null && CONFIG.soulbookUses > 0 && upgrades.get(Upgrade.CRYSTAL_DARK) < 1) {
			soulbookUses -= (float) (spawned * CONFIG.efficiencyUpgradeRange
				.get(upgrades.get(Upgrade.EFFICIENCY) / (double) Upgrade.EFFICIENCY.getMaxQuantity()));
		}

		// reset whether a player was used for this spawn
		usedPlayer = false;

		if (spawned > 0)
			hadInitialSpawn = true;

		return spawned;
	}

	private void explosionParticles (EntityLiving entity) {
		Random rand = world.rand;

		WorldServer worldServer = world.getMinecraftServer().getWorld(entity.dimension);

		for (int i = 0; i < CONFIG.particleCountSpawn; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH, //
				entity.posX + (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width - d0 * 10.0D, // 
				entity.posY + (double) (rand.nextFloat() * entity.height) - d1 * 10.0D, //
				entity.posZ + (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width - d2 * 10.0D, //
				1, //
				d0, d1, d2, // 
				Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2));

			double d3 = (pos.getX() + rand.nextFloat());
			double d4 = (pos.getY() + rand.nextFloat());
			double d5 = (pos.getZ() + rand.nextFloat());
			double d3o = (pos.getX() - d3 - 0.5F) / 20;
			double d4o = (pos.getY() - d4) / 20;
			double d5o = (pos.getZ() - d5 - 0.5F) / 20;
			worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH, //
				d3, d4, d5, // 
				1, //
				d3o, d4o, d5o, //
				Math.sqrt(d3o * d3o + d4o * d4o + d5o * d5o) * 2);
		}
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock() || oldState.getValue(Summoner.HAS_SOULBOOK) != newState
			.getValue(Summoner.HAS_SOULBOOK);
	}
}
