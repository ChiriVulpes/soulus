package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ClientField;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.DefaultClassSerializer;
import yuudaari.soulus.common.util.serializer.DefaultFieldSerializer;
import yuudaari.soulus.common.util.serializer.MapSerializer;
import yuudaari.soulus.common.util.serializer.NullableField;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/summoner", id = Soulus.MODID)
@Serializable
public class ConfigSummoner extends ConfigUpgradeableBlock<Summoner> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return Summoner.Upgrade.values();
	}

	// CLIENT
	@Serialized @ClientField public int particleCountSpawn = 50;
	@Serialized @ClientField public double particleCountActivated = 3;

	// SERVER
	// count
	@Serialized public int nonUpgradedSpawningRadius = 4;
	@Serialized public Range nonUpgradedCount = new Range(1, 2);
	@Serialized public Range upgradeCountEffectiveness = new Range(0.2, 0.5);
	@Serialized public double upgradeCountRadiusEffectiveness = 0.15;
	// range
	@Serialized public int nonUpgradedRange = 4;
	@Serialized public int upgradeRangeEffectiveness = 4;
	// delay
	@Serialized public Range nonUpgradedDelay = new Range(10000, 20000);
	@Serialized public Range upgradeDelayEffectiveness = new Range(0.8, 1);
	// soulbook uses
	@Serialized public double soulbookEssenceRequiredToInsert = 0.5;
	@Serialized @NullableField public Integer soulbookUses = 256;
	@Serialized public Range efficiencyUpgradeRange = new Range(1, 0.3);
	// midnight jewel
	@Serialized public int midnightJewelRange = 16;
	@Serialized public Range midnightJewelCount = new Range(2, 4);
	@Serialized public Range midnightJewelDelay = new Range(500, 1000);
	@Serialized public int midnightJewelSpawningRadius = 4;
	@Serialized public Range midnightJewelSoulbookEssenceQuantity = new Range(1, 5);

	// style potion effects
	@Serialized(PotionEffectsMapSerializer.class) public Map<EndersteelType, ModPotionEffect[]> stylePotionEffects;
	{
		stylePotionEffects = new HashMap<>();

		stylePotionEffects.put(EndersteelType.NORMAL, new ModPotionEffect[] {
			new ModPotionEffect("water_breathing", 6000, 0.1f),
			new ModPotionEffect("strength", 6000, 0.1f),
			new ModPotionEffect("slowness", 6000, 0.1f),
			new ModPotionEffect("invisibility", 6000, 0.1f),
			new ModPotionEffect("weakness", 6000, 0.1f),
			new ModPotionEffect("regeneration", 6000, 0.1f),
			new ModPotionEffect("speed", 6000, 0.1f),
			new ModPotionEffect("resistance", 6000, 0.1f)
		});

		// spooky
		stylePotionEffects.put(EndersteelType.SPOOKY, new ModPotionEffect[] {
			new ModPotionEffect("glowing", 6000, 0.1f),
			new ModPotionEffect("invisibility", 6000, 0.5f),
			new ModPotionEffect("wither", 6000, 0.1f)
		});

		// earthy
		stylePotionEffects.put(EndersteelType.EARTHY, new ModPotionEffect[] {
			new ModPotionEffect("slowness", 6000, 0.2f),
			new ModPotionEffect("resistance", 6000, 0.5f),
			new ModPotionEffect("poison", 6000, 0.1f)
		});

		// blazing
		stylePotionEffects.put(EndersteelType.BLAZING, new ModPotionEffect[] {
			new ModPotionEffect("fire_resistance", 6000, 0.5f),
			new ModPotionEffect("regeneration", 6000, 0.2f),
			new ModPotionEffect("weakness", 6000, 0.1f)
		});

		// ender
		stylePotionEffects.put(EndersteelType.ENDER, new ModPotionEffect[] {
			new ModPotionEffect("speed", 6000, 0.1f),
			new ModPotionEffect("absorption", 6000, 0.5f),
			new ModPotionEffect("strength", 6000, 0.2f)
		});

		// sorrow
		stylePotionEffects.put(EndersteelType.SORROW, new ModPotionEffect[] {
			new ModPotionEffect("slowness", 6000, 0.2f),
			new ModPotionEffect("weakness", 6000, 0.5f),
			new ModPotionEffect("resistance", 6000, 0.1f)
		});

		// madness
		stylePotionEffects.put(EndersteelType.MADNESS, new ModPotionEffect[] {
			new ModPotionEffect("weakness", 6000, 0.1f),
			new ModPotionEffect("wither", 6000, 0.5f),
			new ModPotionEffect("poison", 6000, 0.2f)
		});
	}

	public static class PotionEffectsMapSerializer extends MapSerializer<EndersteelType, ModPotionEffect[]> {

		@Override
		public EndersteelType deserializeKey (String key) {
			return EndersteelType.byName(key);
		}

		@Override
		public String serializeKey (EndersteelType key) {
			return key.getName();
		}

		@Override
		public JsonElement serializeValue (final ModPotionEffect[] value) throws Exception {
			final IFieldSerializationHandler<Object> serializer = new DefaultFieldSerializer();
			return DefaultClassSerializer.serializeValue(serializer, ModPotionEffect[].class, false, value);
		}

		@Override
		public ModPotionEffect[] deserializeValue (final JsonElement value) throws Exception {
			final IFieldDeserializationHandler<Object> deserializer = new DefaultFieldSerializer();
			return (ModPotionEffect[]) DefaultClassSerializer
				.deserializeValue(deserializer, ModPotionEffect[].class, false, value);
		}
	}
}
