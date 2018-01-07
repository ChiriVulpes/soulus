package yuudaari.soulus.common.block.composer;

import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.block.UpgradeableBlock.UpgradeableBlockTileEntity;

public abstract class HasRenderItemTileEntity extends UpgradeableBlockTileEntity {
	public abstract double getPrevItemRotation();

	public abstract double getItemRotation();

	public abstract ItemStack getStoredItem();

	public boolean shouldComplexRotate() {
		return false;
	}
}