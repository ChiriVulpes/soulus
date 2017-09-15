package yuudaari.souls.common.block;

import yuudaari.souls.common.util.IModItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

public interface IBlock extends IModItem {
	abstract boolean hasItem();

	abstract void setHasItem();

	abstract ItemBlock getItemBlock();

	abstract boolean hasTileEntity();

	abstract Class<? extends TileEntity> getTileEntityClass();
}