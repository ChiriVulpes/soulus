package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.block.composer.ComposerCell;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/composer", id = Soulus.MODID, path = "cell")
@Serializable
public class ConfigComposerCell extends ConfigUpgradeableBlock<ComposerCell> {

	@Override
	protected IUpgrade[] getUpgrades () {
		return new IUpgrade[0];
	}

	@Serialized public int maxQuantity = 64;
}
