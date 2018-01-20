package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.skewer.Skewer;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/skewer", id = Soulus.MODID)
@Serializable
public class ConfigSkewer extends ConfigUpgradeableBlock<Skewer> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return Skewer.Upgrade.values();
	}

	@Serialized public float baseDamage = 1;
	@Serialized public float upgradeDamageEffectiveness = 0.16f;
	@Serialized public int bloodPerDamage = 1;
	@Serialized public float chanceForBloodPerHit = 0.5f;
	@Serialized public int ticksBetweenDamage = 15;
	@Serialized public Range tetherChance = new Range(0.2, 1);
}
