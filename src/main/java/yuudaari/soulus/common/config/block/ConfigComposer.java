package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.composer.Composer;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.ListSerializer;
import yuudaari.soulus.common.util.serializer.NullableField;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import java.util.List;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/composer", id = Soulus.MODID)
@Serializable
public class ConfigComposer extends ConfigUpgradeableBlock<Composer> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return Composer.Upgrade.values();
	}

	@Serialized public Range nonUpgradedDelay = new Range(500, 1000);
	@Serialized public int nonUpgradedRange = 4;
	@Serialized public Range upgradeDelayEffectiveness = new Range(0.8, 1);
	@Serialized public int upgradeRangeEffectiveness = 1;
	@Serialized public double particleCountActivated = 1;
	@Serialized public int particleCountMax = 6;
	@Serialized public int particleCountMobPoof = 50;
	@Serialized public Range poofChance = new Range(0.001, 0.0003);
	@Serialized(ListSerializer.OfStrings.class) @NullableField public List<String> whitelistedCreatures;
	@Serialized(ListSerializer.OfStrings.class) @NullableField public List<String> blacklistedCreatures;
}
