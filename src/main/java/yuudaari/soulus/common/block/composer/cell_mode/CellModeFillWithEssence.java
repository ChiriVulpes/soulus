package yuudaari.soulus.common.block.composer.cell_mode;

import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;
import yuudaari.soulus.common.block.composer.IFillableWithEssence;
import yuudaari.soulus.common.registration.ItemRegistry;

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
	public boolean tryInsert (final ItemStack stack, final int requestedQuantity) {
		if (stack.getItem() != ItemRegistry.ESSENCE && stack.getItem() != ItemRegistry.ASH)
			return false;

		final IFillableWithEssence fillable = (IFillableWithEssence) cell.storedItem.getItem();
		final int insertQuantity = fillable.fill(cell.storedItem, stack, requestedQuantity);
		if (insertQuantity > 0) {
			stack.shrink(insertQuantity);
			cell.blockUpdate();
		}

		return true;
	}
}
