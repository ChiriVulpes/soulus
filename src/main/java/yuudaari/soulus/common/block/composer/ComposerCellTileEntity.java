package yuudaari.soulus.common.block.composer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ComposerCellTileEntity extends HasRenderItemTileEntity {

	public ChangeItemHandler changeItemHandler;
	public BlockPos composerLocation;
	public int changeComposerCooldown = 0;
	public byte slot = -1;

	public ItemStack storedItem;
	public int storedQuantity;

	@Override
	public ComposerCell getBlock () {
		return ComposerCell.INSTANCE;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update () {
		changeComposerCooldown--;

		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation = itemRotation + 0.05F + diff * 0.8;
	}

	public void onChangeItem () {
		if (composerLocation == null)
			return;
		TileEntity te = world.getTileEntity(composerLocation);
		if (te == null || !(te instanceof ComposerTileEntity) || !((ComposerTileEntity) te).isConnected())
			return;

		if (changeItemHandler != null)
			changeItemHandler.handle(this);
	}

	public void onChangeItem (ChangeItemHandler handler) {
		changeItemHandler = handler;
	}

	public static interface ChangeItemHandler {

		public Boolean handle (ComposerCellTileEntity ccte);
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
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

		compound.setByte("slot", slot);
	}

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		if (compound.getBoolean("has_composer")) {
			composerLocation = new BlockPos(compound.getInteger("composer_x"), compound
				.getInteger("composer_y"), compound.getInteger("composer_z"));
		}

		storedQuantity = compound.getInteger("stored_quantity");
		if (storedQuantity > 0) {
			storedItem = new ItemStack(compound.getCompoundTag("stored_item"));
		} else {
			storedItem = null;
		}

		slot = compound.getByte("slot");
	}

	/////////////////////////////////////////
	// Renderer
	//

	private double itemRotation = Math.random() * 360;
	private double prevItemRotation = 0;

	@Override
	public double getItemRotation () {
		return itemRotation;
	}

	@Override
	public double getPrevItemRotation () {
		return prevItemRotation;
	}

	@Override
	public ItemStack getStoredItem () {
		return storedItem;
	}
}
