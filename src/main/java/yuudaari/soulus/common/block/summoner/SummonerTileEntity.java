package yuudaari.soulus.common.block.summoner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
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
import yuudaari.soulus.common.ModBlocks;
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
import yuudaari.soulus.common.item.EssencePerfect;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.Range;

@ConfigInjected(Soulus.MODID)
public class SummonerTileEntity extends UpgradeableBlockTileEntity implements ITickable {

	@Inject public static ConfigSummoner CONFIG;
	@Inject public static ConfigEssences CONFIG_ESSENCES;

	@Override
	public Summoner getBlock () {
		return ModBlocks.SUMMONER;
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
			soulbookUses = (float) (int) CONFIG.soulbookUses;
		return soulbookUses;
	}

	public void setSoulbookUses (Float val) {
		soulbookUses = val;
	}

	private int spawningRadius;
	private int activatingRange;
	private Range spawnDelay;
	private Range spawnCount;
	private boolean usedPlayer = false;

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

	@Override
	public void onUpdateUpgrades (boolean readFromNBT) {
		if (isInvalid()) {
			Soulus.removeConfigReloadHandler(this::onUpdateUpgrades);
			return;
		}

		Soulus.onConfigReload(this::onUpdateUpgrades);

		if (upgrades.get(Upgrade.CRYSTAL_DARK) > 0) {
			// dark crystal
			spawnCount = CONFIG.midnightJewelCount;
			spawningRadius = CONFIG.midnightJewelSpawningRadius;
			spawnDelay = CONFIG.midnightJewelDelay;
			activatingRange = CONFIG.midnightJewelRange;

		} else {
			// normal upgrades
			int countUpgrades = upgrades.get(Upgrade.COUNT);
			spawnCount = new Range(CONFIG.nonUpgradedCount.min + countUpgrades * CONFIG.upgradeCountEffectiveness.min, CONFIG.nonUpgradedCount.max + countUpgrades * CONFIG.upgradeCountEffectiveness.max);
			spawningRadius = (int) Math.floor(CONFIG.nonUpgradedSpawningRadius + countUpgrades * CONFIG.upgradeCountRadiusEffectiveness);

			int delayUpgrades = upgrades.get(Upgrade.DELAY);
			spawnDelay = new Range(CONFIG.nonUpgradedDelay.min / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.min), CONFIG.nonUpgradedDelay.max / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.max));

			int rangeUpgrades = upgrades.get(Upgrade.RANGE);
			activatingRange = CONFIG.nonUpgradedRange + rangeUpgrades * CONFIG.upgradeRangeEffectiveness;
		}


		if (world != null && !world.isRemote) {
			if (!readFromNBT)
				resetTimer(false);
			blockUpdate();
		}

	}

	private ConfigEssence spawnMobConfig;
	private int spawnMobChanceTotal;
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

		spawnMobChanceTotal = 0;

		if (spawnMobConfig != null && spawnMobConfig.spawns != null) {
			for (double dropChance : spawnMobConfig.spawns.values()) {
				spawnMobChanceTotal += dropChance;
			}
		}
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
		if (spawnMobChanceTotal > 0 && spawnMobConfig != null) {
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

	public boolean insertPerfectEssence (final ItemStack stack, final boolean all) {
		final String essenceType = getEssenceType();
		final String[] perfectEssenceTypes = EssencePerfect.getEssenceTypes(stack);

		if (!Arrays.stream(perfectEssenceTypes).anyMatch(essenceType::equalsIgnoreCase))
			return false;

		final int count = all ? stack.getCount() : 1;

		for (int i = 0; i < count; i++) {
			boost += CONFIG.perfectEssenceBoost * spawnDelay.get(world.rand);
		}

		blockUpdate();
		timeTillNextMajorUpdate = 0;

		stack.shrink(count);
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

		if (timeTillSpawn <= 0) {
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
		resetEssenceType();
		timeTillSpawn = compound.getFloat("delay");
		lastTimeTillSpawn = compound.getFloat("delay_last");
		malice = compound.getBoolean("malice");
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
		compound.setBoolean("used_player", usedPlayer);
		compound.setFloat("boost", boost);
	}

	private boolean isPlayerInRangeForEffects () {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 64);
	}

	private void updateRenderer (double activationAmount) {
		double diff = mobRotation - prevMobRotation;
		prevMobRotation = mobRotation;
		mobRotation += activationAmount <= 0 ? diff * 0.9 : getSpawnPercent() + diff * 0.8;

		if (activationAmount <= 0) return;

		if (isPlayerInRangeForEffects()) {
			double particleCount = CONFIG.particleCountActivated * Math
				.min(1, activationAmount * activationAmount) * (0.5 + getSpawnPercent() / 2);
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
				if (world.getEntitiesWithinAABB(entity.getClass(), boundingBox)
					.size() >= Math.pow(spawningRadius * 2, 2) / 8) {
					// we can return here because this check won't change next loop
					break MainSpawningLoop;
				}

				// check if there's blocks in the way
				if (world.collidesWithAnyBlock(entity.getEntityBoundingBox())) {
					// we chose a bad position, so we continue and try again
					continue;
				}

				// custom data so we know the mob was spawned by souls
				NBTTagCompound entityData = entity.getEntityData();
				entityData.setByte("soulus:spawn_whitelisted", (byte) 2);

				entity
					.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);


				ModPotionEffect[] potionEffects = CONFIG.stylePotionEffects
					.get(world.getBlockState(pos).getValue(Summoner.VARIANT));
				if (potionEffects != null) for (ModPotionEffect effect : potionEffects) {
					effect.apply(entity);
				}

				LivingSpawnEvent.SpecialSpawn specialSpawn = new LivingSpawnEvent.SpecialSpawn(entity, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ, null);
				if (!MinecraftForge.EVENT_BUS.post(specialSpawn)) {
					entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
				}

				AnvilChunkLoader.spawnEntity(entity, world);

				if (isPlayerInRangeForEffects())
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
