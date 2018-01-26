package yuudaari.soulus.common.config.block;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.soul_totem.SoulTotem;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "block/soul_totem", id = Soulus.MODID)
@Serializable
public class ConfigSoulTotem extends ConfigUpgradeableBlock<SoulTotem> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return SoulTotem.Upgrade.values();
	}

	@Serialized public float particleCountIn = 1;
	@Serialized public int soulCatalystFuelTime = 5000;
	@Serialized public Range efficiencyUpgradesRange = new Range(1, .3);

}
