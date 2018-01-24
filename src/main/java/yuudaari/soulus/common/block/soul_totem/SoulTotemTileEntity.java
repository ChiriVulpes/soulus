package yuudaari.soulus.common.block.soul_totem;

import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.soul_totem.SoulTotem.Upgrade;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;

public class SoulTotemTileEntity extends UpgradeableBlockTileEntity {

	@Override
	public UpgradeableBlock<? extends UpgradeableBlockTileEntity> getBlock () {
		return ModBlocks.SOUL_TOTEM;
	}

	public boolean isActive () {
		return upgrades.get(Upgrade.SOUL_CATALYST) == 1;
	}

	@Override
	public void update () {

	}

	public int getSignalStrength () {
		return isActive() ? 15 : 0;
	}
}
