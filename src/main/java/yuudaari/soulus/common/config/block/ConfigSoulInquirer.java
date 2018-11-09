package yuudaari.soulus.common.config.block;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.soul_inquirer.SoulInquirer;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ClientField;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "block/soul_inquirer", id = Soulus.MODID)
@Serializable
public class ConfigSoulInquirer extends ConfigUpgradeableBlock<SoulInquirer> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return SoulInquirer.Upgrade.values();
	}

	// CLIENT
	@Serialized @ClientField public double particleCount = 1;

	// SERVER
	// range
	@Serialized public int nonUpgradedRange = 4;
	@Serialized public int upgradeRangeEffectiveness = 2;
}
