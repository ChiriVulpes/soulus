package yuudaari.soulus.common.block.composer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;

@ConfigInjected(Soulus.MODID)
public class ComposerCellTileEntity extends HasRenderItemTileEntity {

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigComposerCell CONFIG;

	public ChangeItemHandler changeItemHandler;
	public BlockPos composerLocation;
	public int changeComposerCooldown = 0;
	public byte slot = -1;

	@Nullable public ItemStack storedItem;
	public int storedQuantity;

	@Override
	public ComposerCell getBlock () {
		return ModBlocks.COMPOSER_CELL;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean shouldCheckSignal = true;
	private boolean lastSignalIn = false;

	@Override
	public void update () {
		changeComposerCooldown--;

		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation = itemRotation + 0.05F + diff * 0.8;

		if (!world.isRemote && storedQuantity < CONFIG.maxQuantity)
			pullItems();

		if (!shouldCheckSignal || world.isRemote)
			return;

		shouldCheckSignal = false;

		final boolean signalIn = world.isBlockIndirectlyGettingPowered(pos) > 0;
		if (signalIn && !lastSignalIn && storedItem != null) {
			UpgradeableBlockTileEntity.dispenseItem(storedItem.copy(), world, pos, EnumFacing.DOWN);
			storedQuantity--;
			if (storedQuantity == 0) storedItem = null;
			onChangeItem();
			blockUpdate();
		}

		lastSignalIn = signalIn;
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

	public boolean pullItems () {

		for (final EntityItem item : getCaptureItems()) {
			if (item.ticksExisted < 2) continue;
			final ItemStack stack = item.getItem();
			if (tryInsert(stack, stack.getCount())) return true;
		}

		return false;
	}

	public boolean tryInsert (final ItemStack stack, final int requestedQuantity) {
		if (stack == null || stack.isEmpty()) return false;

		ItemStack currentStack = storedItem;
		if (currentStack != null && currentStack.isEmpty()) currentStack = storedItem = null;

		if (currentStack == null || areItemStacksEqual(stack, currentStack)) {
			final int canStillBeInsertedQuantity = CONFIG.maxQuantity - (currentStack == null ? 0 : storedQuantity);
			final int insertQuantity = Math.min(requestedQuantity, canStillBeInsertedQuantity);

			if (currentStack == null) {
				storedItem = stack.copy();
				storedItem.setCount(1);
				storedQuantity = insertQuantity;
			} else {
				storedQuantity += insertQuantity;
			}

			stack.shrink(insertQuantity);
			onChangeItem();
			blockUpdate();

			return true;

		} else if (composerLocation == null && //
			currentStack.getItem() instanceof IFillableWithEssence && storedQuantity == 1 && //
			(stack.getItem() == ModItems.ESSENCE || stack.getItem() == ModItems.ASH)) {

			final IFillableWithEssence fillable = (IFillableWithEssence) currentStack.getItem();
			final int insertQuantity = fillable.fill(currentStack, stack, requestedQuantity);
			if (insertQuantity > 0) {
				stack.shrink(insertQuantity);
				blockUpdate();
			}
		}

		return false;
	}

	/////////////////////////////////////////
	// Utility
	//

	public static boolean areItemStacksEqual (ItemStack stackA, ItemStack stackB) {
		if (stackA.getItem() != stackB.getItem()) {
			return false;
		} else if (stackA.getItemDamage() != stackB.getItemDamage()) {
			return false;
		} else if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
			return false;
		} else {
			return (stackA.getTagCompound() == null || stackA.getTagCompound()
				.equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
		}
	}

	public List<EntityItem> getCaptureItems () {
		return world
			.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos)
				.offset(new BlockPos(EnumFacing.UP.getDirectionVec()))
				.contract(0, 0.8, 0), EntitySelectors.IS_ALIVE);
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
	@Nullable
	public ItemStack getStoredItem () {
		return storedItem;
	}
}
