package yuudaari.soulus.common.block.composer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ComposerCellTileEntity extends HasRenderItemTileEntity {

	public BlockPos composerLocation;

	public ItemStack storedItem;
	public int storedQuantity;
	private double itemRotation = Math.random() * 360;
	private double prevItemRotation = 0;

	@Override
	public double getItemRotation() {
		return itemRotation;
	}

	@Override
	public double getPrevItemRotation() {
		return prevItemRotation;
	}

	@Override
	public ItemStack getStoredItem() {
		return storedItem;
	}

	@Override
	public ComposerCell getBlock() {
		return ComposerCell.INSTANCE;
	}

	@Override
	public void update() {
		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation = itemRotation + 0.05F + diff * 0.8;
	}

	@Override
	public void onWriteToNBT(NBTTagCompound compound) {
		compound.setBoolean("has_composer", composerLocation != null);

		if (composerLocation != null) {
			compound.setInteger("composer_x", composerLocation.getX());
			compound.setInteger("composer_y", composerLocation.getY());
			compound.setInteger("composer_z", composerLocation.getZ());
		}

		compound.setInteger("stored_quantity", storedQuantity);

		if (storedQuantity > 0) {
			compound.setTag("stored_item", storedItem.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void onReadFromNBT(NBTTagCompound compound) {
		if (compound.getBoolean("has_composer")) {
			composerLocation = new BlockPos(compound.getInteger("composer_x"), compound.getInteger("composer_y"),
					compound.getInteger("composer_z"));
		}

		storedQuantity = compound.getInteger("stored_quantity");
		if (storedQuantity > 0) {
			storedItem = new ItemStack(compound.getCompoundTag("stored_item"));
		} else {
			storedItem = null;
		}
	}
}