package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/summoner", id = Soulus.MODID)
@Serializable
public class ConfigSummoner extends ConfigUpgradeableBlock<Summoner> {

	@Override
	protected Summoner getBlock () {
		return Summoner.INSTANCE;
	}

	@Serialized public int nonUpgradedSpawningRadius = 4;
	@Serialized public Range nonUpgradedCount = new Range(1, 2);
	@Serialized public Range nonUpgradedDelay = new Range(10000, 20000);
	@Serialized public int nonUpgradedRange = 4;
	@Serialized public Range upgradeCountEffectiveness = new Range(0.2, 0.5);
	@Serialized public double upgradeCountRadiusEffectiveness = 0.15;
	@Serialized public int upgradeRangeEffectiveness = 4;
	@Serialized public double particleCountActivated = 3;
	@Serialized public int particleCountSpawn = 50;
	@Serialized public int soulbookUses = -1;
	@Serialized public double soulbookEssenceRequiredToInsert = 0.5;
	@Serialized public Range upgradeDelayEffectiveness = new Range(0.8, 1);
}
