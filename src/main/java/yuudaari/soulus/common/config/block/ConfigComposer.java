package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.composer.Composer;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/composer", id = Soulus.MODID)
@Serializable
public class ConfigComposer extends ConfigUpgradeableBlock<Composer> {

	@Override
	protected Composer getBlock () {
		return Composer.INSTANCE;
	}

	@Serialized public Range nonUpgradedCount = new Range(1, 2);
	@Serialized public Range nonUpgradedDelay = new Range(500, 1000);
	@Serialized public int nonUpgradedRange = 4;
	@Serialized public Range upgradeDelayEffectiveness = new Range(0.8, 1);
	@Serialized public int upgradeRangeEffectiveness = 1;
	@Serialized public int particleCountActivated = 3;
	@Serialized public int particleCountMobPoof = 50;
	@Serialized public double poofChance = 0.001;
}
