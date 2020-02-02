package yuudaari.soulus.common.block.composer.cell_mode;

import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;
import yuudaari.soulus.common.block.composer.IFillableWithEssence;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.ItemStackMutable;

/**
 * Auto-fills items with essence or ash
 */
public class CellModeFillWithEssence extends ComposerCellTileEntity.Mode {

	@Override
	public String getName () {
		return "fill_with_essence";
	}

	@Override
	public boolean isActive () {
		return !cell.isConnected() //
			&& cell.storedItem != null && cell.storedItem.getItem() instanceof IFillableWithEssence //
			&& cell.storedQuantity == 1;
	}

	@Override
	public boolean tryInsert (final ItemStackMutable stack, final int requestedQuantity, final boolean isPulling) {
		if (stack.getItem() != ItemRegistry.ESSENCE && stack.getItem() != ItemRegistry.ASH)
			return false;

		final IFillableWithEssence fillable = (IFillableWithEssence) cell.storedItem.getItem();
		final int insertQuantity = fillable.fillWithEssence(cell.storedItem, stack.getImmutable(), requestedQuantity);
		if (insertQuantity > 0) {
			stack.shrink(insertQuantity);
			cell.blockUpdate();
		}

		if (fillable.isFilledWithEssence(cell.storedItem))
			Advancements.COMPOSER_CELL_AUTO_FILL_TRIGGER.trigger(cell.getOwner(), null);

		return true;
	}
}
