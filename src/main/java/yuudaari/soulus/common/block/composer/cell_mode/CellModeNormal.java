package yuudaari.soulus.common.block.composer.cell_mode;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.util.ItemStackMutable;

@ConfigInjected(Soulus.MODID)
public class CellModeNormal extends ComposerCellTileEntity.Mode {

	@Inject public static ConfigComposerCell CONFIG;

	@Override
	public String getName () {
		return "normal";
	}

	@Override
	public boolean isActive () {
		return true;
	}

	@Override
	public boolean tryInsert (final ItemStackMutable stack, final int requestedQuantity, final boolean isPulling) {
		if (cell.storedItem != null && !ComposerCellTileEntity.areItemStacksEqual(stack.getImmutable(), cell.storedItem))
			return false;

		final int canStillBeInsertedQuantity = CONFIG.maxQuantity - (cell.storedItem == null ? 0 : cell.storedQuantity);
		final int insertQuantity = Math.min(requestedQuantity, canStillBeInsertedQuantity);

		if (cell.storedItem == null) {
			cell.storedItem = stack.copy();
			cell.storedItem.setCount(1);
			cell.storedQuantity = insertQuantity;
		} else {
			cell.storedQuantity += insertQuantity;
		}

		stack.shrink(insertQuantity);
		cell.onChangeItem();
		cell.blockUpdate();

		return true;
	}
}
