package yuudaari.soulus.common.block.summoner;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.config.CreatureConfig;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.Range;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SummonerTileEntity extends TileEntity implements ITickable {

	public static enum Upgrade {
		COUNT, DELAY, RANGE
	}

	/* CONFIGS */
	public static class Config {
		public int nonUpgradedSpawningRadius = 4;
		public Range nonUpgradedCount = new Range(1, 2);
		public Range nonUpgradedDelay = new Range(10000, 20000);
		public int nonUpgradedRange = 4;
		public Map<Upgrade, Integer> maxUpgrades = new HashMap<>();
		{
			maxUpgrades.put(Upgrade.COUNT, 64);
			maxUpgrades.put(Upgrade.DELAY, 64);
			maxUpgrades.put(Upgrade.RANGE, 64);
		}
		public Range upgradeCountEffectiveness = new Range(0.3, 1);
		public double upgradeCountRadiusEffectiveness = 0.15;
		public Range upgradeDelayEffectiveness = new Range(0.8, 1);
		public int upgradeRangeEffectiveness = 4;
		public double particleCountActivated = 3;
		public int particleCountSpawn = 50;

		private static Serializer<Config> serializer;
		static {
			serializer = new Serializer<>(Config.class, "nonUpgradedSpawningRadius", "nonUpgradedRange",
					"upgradeCountRadiusEffectiveness", "upgradeRangeEffectiveness", "particleCountActivated",
					"particleCountSpawn");

			serializer.fieldHandlers.put("nonUpgradedCount", Range.serializer);
			serializer.fieldHandlers.put("nonUpgradedDelay", Range.serializer);
			serializer.fieldHandlers.put("upgradeCountEffectiveness", Range.serializer);
			serializer.fieldHandlers.put("upgradeDelayEffectiveness", Range.serializer);

			serializer.fieldHandlers.put("maxUpgrades", new ManualSerializer(SummonerTileEntity::serializeMaxUpgrades,
					SummonerTileEntity::deserializeMaxUpgrades));
		}
	}

	private static JsonElement serializeMaxUpgrades(Object from) {
		@SuppressWarnings("unchecked")
		Map<Upgrade, Integer> upgrades = (Map<Upgrade, Integer>) from;

		JsonObject result = new JsonObject();

		for (Map.Entry<Upgrade, Integer> upgrade : upgrades.entrySet()) {
			String key = upgrade.getKey().name().toLowerCase();
			result.addProperty(key, upgrade.getValue());
		}

		return result;
	}

	private static Object deserializeMaxUpgrades(JsonElement from, Object current) {
		if (from == null || !from.isJsonObject()) {
			Logger.warn("Max upgrades must be an object");
			return current;
		}

		@SuppressWarnings("unchecked")
		Map<Upgrade, Integer> currentUpgrades = (Map<Upgrade, Integer>) current;

		JsonObject upgrades = from.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : upgrades.entrySet()) {
			JsonElement val = entry.getValue();

			if (val == null || !val.isJsonPrimitive() || !val.getAsJsonPrimitive().isNumber()) {
				Logger.warn("Upgrade maximum must be an number");
				continue;
			}

			String key = entry.getKey();
			Upgrade upgrade = null;
			for (Upgrade checkUpgrade : Upgrade.values()) {
				if (key.equalsIgnoreCase(checkUpgrade.name())) {
					upgrade = checkUpgrade;
				}
			}
			if (upgrade == null) {
				Logger.warn("Upgrade type '" + key + "' is invalid");
				continue;
			}

			currentUpgrades.put(upgrade, val.getAsInt());
		}

		return current;
	}

	public static JsonElement serialize() {
		return Config.serializer.serialize(config);
	}

	public static Object deserialize(JsonElement from) {
		config = (Config) Config.serializer.deserialize(from, config);
		return null;
	}

	private static Config config = new Config();

	/* OTHER */
	private boolean hasInit = false;
	private String spawnMob;
	private float timeTillSpawn = 0;
	private float lastTimeTillSpawn;
	private Map<Upgrade, Integer> upgradeCounts = new HashMap<>();
	{
		upgradeCounts.put(Upgrade.COUNT, 0);
		upgradeCounts.put(Upgrade.DELAY, 0);
		upgradeCounts.put(Upgrade.RANGE, 0);
	}
	private Stack<Upgrade> insertionOrder = new Stack<>();

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

	public int getUpgradeCount(Upgrade upgradeType) {
		return upgradeCounts.get(upgradeType);
	}

	public boolean addUpgrade(Upgrade upgradeType) {
		boolean result = addUpgradeStack(upgradeType, 1) == 1;
		updateUpgrades();
		return result;
	}

	public int addUpgradeStack(Upgrade upgradeType, int count) {
		int oldCount = upgradeCounts.get(upgradeType);
		int maximum = config.maxUpgrades.get(upgradeType);
		int newCount = oldCount + count;
		if (newCount > maximum)
			newCount = maximum;
		upgradeCounts.put(upgradeType, newCount);
		updateUpgrades();
		updateInsertionOrder(upgradeType);
		return newCount - oldCount;
	}

	public Upgrade getLastInserted() {
		return insertionOrder.size() == 0 ? null : insertionOrder.peek();
	}

	public int removeUpgrade(Upgrade upgradeType) {
		int result = upgradeCounts.get(upgradeType);
		upgradeCounts.put(upgradeType, 0);
		updateInsertionOrder(upgradeType);
		updateUpgrades();
		return result;
	}

	private void setUpgradeCount(Upgrade upgradeType, int newCount) {
		int maximum = config.maxUpgrades.get(upgradeType);
		if (newCount > maximum)
			newCount = maximum;
		upgradeCounts.put(upgradeType, newCount);
	}

	private void updateInsertionOrder(Upgrade upgradeType) {
		insertionOrder.remove(upgradeType);
		if (upgradeCounts.get(upgradeType) > 0) {
			insertionOrder.push(upgradeType);
		}
	}

	private void updateUpgrades() {
		updateUpgrades(true);
	}

	private void updateUpgrades(boolean resetTimer) {

		int countUpgrades = upgradeCounts.get(Upgrade.COUNT);
		spawnCount = new Range(config.nonUpgradedCount.min + countUpgrades * config.upgradeCountEffectiveness.min,
				config.nonUpgradedCount.max + countUpgrades * config.upgradeCountEffectiveness.max);
		spawningRadius = (int) Math
				.floor(config.nonUpgradedSpawningRadius + countUpgrades * config.upgradeCountRadiusEffectiveness);

		int delayUpgrades = upgradeCounts.get(Upgrade.DELAY);
		spawnDelay = new Range(config.nonUpgradedDelay.min / (1 + delayUpgrades * config.upgradeDelayEffectiveness.min),
				config.nonUpgradedDelay.max / (1 + delayUpgrades * config.upgradeDelayEffectiveness.max));

		int rangeUpgrades = upgradeCounts.get(Upgrade.RANGE);
		activatingRange = config.nonUpgradedRange + rangeUpgrades * config.upgradeRangeEffectiveness;

		if (world != null && !world.isRemote) {
			if (resetTimer)
				resetTimer(false);
			blockUpdate();
		}

	}

	private CreatureConfig spawnMobConfig;
	private int spawnMobChanceTotal;

	public String getMob() {
		return spawnMob;
	}

	public void setMob(String mobName) {
		spawnMob = mobName;
		resetSpawnMob();
	}

	private void resetSpawnMob() {
		for (CreatureConfig config : Soulus.config.creatures) {
			if (config.essence.equals(spawnMob)) {
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
		//.setString("id", spawnMob).nbt;
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
		return spawnMob;
	}

	private float getSpawnPercent() {
		return (lastTimeTillSpawn - timeTillSpawn) / (float) lastTimeTillSpawn;
	}

	private double activationAmount() {
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) {
			return 0;
		}

		double activationAmount = 0;

		for (EntityPlayer player : world.playerEntities) {

			if (EntitySelectors.NOT_SPECTATING.apply(player)) {
				double d0 = player.getDistanceSqToCenter(pos);

				double nearAmt = (d0 / (activatingRange * activatingRange));
				activationAmount += (1 - (nearAmt * nearAmt)) * 2;
			}
		}

		return Math.max(0, activationAmount);
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
		resetSpawnMob();
		timeTillSpawn = compound.getFloat("delay");
		lastTimeTillSpawn = compound.getFloat("delay_last");
		NBTTagCompound upgradeTag = compound.getCompoundTag("upgrades");
		setUpgradeCount(Upgrade.COUNT, upgradeTag.getByte("count"));
		setUpgradeCount(Upgrade.DELAY, upgradeTag.getByte("delay"));
		setUpgradeCount(Upgrade.RANGE, upgradeTag.getByte("range"));
		updateUpgrades(false);

		this.insertionOrder = new Stack<>();
		NBTTagList value = (NBTTagList) compound.getTag("insertion_order");
		for (NBTBase s : value) {
			if (s instanceof NBTTagString) {
				String str = ((NBTTagString) s).getString();
				for (Upgrade u : Upgrade.values()) {
					if (u.name().equals(str)) {
						this.insertionOrder.add(u);
					}
				}
			}
		}
		/*
		String[] insertionOrder = new NBTHelper(compound).getStringArray("insertion_order");
		for (String s : insertionOrder) {
			for (Upgrade u : Upgrade.values()) {
				if (u.name().equals(s)) {
					this.insertionOrder.add(u);
				}
			}
		}
		*/
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (spawnMob == null) {
			world.setBlockState(pos, ModBlocks.SUMMONER_EMPTY.getDefaultState());
			return compound;
		}

		super.writeToNBT(compound);

		//NBTHelper result = new NBTHelper(compound);
		compound.setString("entity_type", spawnMob);
		compound.setFloat("delay", timeTillSpawn);
		compound.setFloat("delay_last", lastTimeTillSpawn);

		//NBTHelper upgrades = new NBTHelper();
		NBTTagCompound upgrades = new NBTTagCompound();
		upgrades.setByte("count", (byte) (int) upgradeCounts.get(Upgrade.COUNT));
		upgrades.setByte("delay", (byte) (int) upgradeCounts.get(Upgrade.DELAY));
		upgrades.setByte("range", (byte) (int) upgradeCounts.get(Upgrade.RANGE));
		compound.setTag("upgrades", upgrades);

		List<String> insertionOrder = new ArrayList<>();
		for (Upgrade u : this.insertionOrder) {
			insertionOrder.add(u.name());
		}

		NBTTagList list = new NBTTagList();
		for (String s : insertionOrder) {
			list.appendTag(new NBTTagString(s));
		}
		compound.setTag("insertion_order", list);
		//result.setStringArray("insertion_order", insertionOrder.toArray(new String[0]));

		return compound;
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
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
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
			if (config.particleCountActivated < 1) {
				timeTillParticle += config.particleCountActivated;

				if (timeTillParticle < 1)
					return;
			}

			timeTillParticle = 0;

			for (int i = 0; i < config.particleCountActivated; i++) {
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

		return spawned;
	}

	private void explosionParticles(EntityLiving entity) {
		Random rand = world.rand;

		WorldServer worldServer = world.getMinecraftServer().getWorld(entity.dimension);

		for (int i = 0; i < config.particleCountSpawn; ++i) {
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

	@SideOnly(Side.CLIENT)
	public List<String> getWailaTooltip(List<String> currenttip, boolean isSneaking) {
		currenttip.add(I18n.format("waila." + Soulus.MODID + ":summoner.summon_percentage",
				(int) Math.floor(getSpawnPercent() * 100)));

		if (isSneaking) {
			List<Upgrade> upgrades = new ArrayList<>(Arrays.asList(Upgrade.values()));
			for (Upgrade upgrade : Lists.reverse(insertionOrder)) {
				upgrades.remove(upgrade);
				currenttip
						.add(I18n.format("waila." + Soulus.MODID + ":summoner.upgrades_" + upgrade.name().toLowerCase(),
								upgradeCounts.get(upgrade), config.maxUpgrades.get(upgrade)));
			}
			for (Upgrade upgrade : upgrades) {
				currenttip
						.add(I18n.format("waila." + Soulus.MODID + ":summoner.upgrades_" + upgrade.name().toLowerCase(),
								upgradeCounts.get(upgrade), config.maxUpgrades.get(upgrade)));
			}
		} else {
			currenttip.add(
					I18n.format("waila." + Soulus.MODID + ":summoner.show_upgrades", upgradeCounts.get(Upgrade.RANGE)));
		}

		return currenttip;
	}
}