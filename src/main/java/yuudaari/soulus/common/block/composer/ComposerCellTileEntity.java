package yuudaari.soulus.common.block.composer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeAutoMarrow;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeFillWithEssence;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeNormal;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.Classes;
import yuudaari.soulus.common.util.Translation;

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
		return BlockRegistry.COMPOSER_CELL;
	}

	public World getWorld () {
		return world;
	}

	public BlockPos getPos () {
		return pos;
	}

	public boolean isConnected () {
		return composerLocation != null;
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

		tryDispenseDueToRedstonePower();

		for (final Mode mode : MODES.values())
			if (mode.shouldAlwaysUpdate() || mode.isActive())
				mode.update();
	}

	private void tryDispenseDueToRedstonePower () {
		if (!shouldCheckSignal || world.isRemote)
			return;

		shouldCheckSignal = false;

		final boolean signalIn = world.isBlockIndirectlyGettingPowered(pos) > 0;
		if (signalIn && !lastSignalIn && storedItem != null && !isStoredItemLockedInside()) {
			dispenseItem(storedItem.copy(), world, pos, EnumFacing.DOWN);
			storedQuantity--;
			if (storedQuantity == 0) storedItem = null;
			onChangeItem();
			blockUpdate();
		}

		lastSignalIn = signalIn;
	}

	private boolean isStoredItemLockedInside () {
		return MODES.values()
			.stream()
			.anyMatch(mode -> mode.isActive() && mode.isLockingStoredItem());
	}

	public void onChangeItem () {
		if (!isConnected())
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
			if (item.ticksExisted < 2)
				continue;

			final ItemStack stack = item.getItem();
			if (tryInsert(stack, stack.getCount()))
				return true;
		}

		return false;
	}

	public boolean tryInsert (final ItemStack stack, final int requestedQuantity) {
		if (stack == null || stack.isEmpty())
			return false;

		if (storedItem != null && storedItem.isEmpty())
			storedItem = null;

		final int maxQuantityAllowed = MODES.values()
			.stream()
			.filter(mode -> mode.isActive())
			.map(mode -> mode.getMaxContainedQuantityForOtherModes(stack))
			.min(Integer::compare)
			.orElse(Integer.MAX_VALUE);

		final int maxInsertAllowed = maxQuantityAllowed - storedQuantity;

		// insert the item!
		for (final Mode mode : MODES.values())
			if (mode.isActive() && mode.tryInsert(stack, Math.min(requestedQuantity, maxInsertAllowed)))
				return true;

		return false;
	}

	public boolean tryExtract (final List<ItemStack> extracted) {
		if (!isStoredItemLockedInside()) {
			addItemStackToList(storedItem, extracted, storedQuantity);
			storedItem = null;
			storedQuantity = 0;
			return true;
		}

		for (final Mode mode : MODES.values())
			if (mode.tryExtract(extracted))
				return true;

		return false;
	}


	////////////////////////////////////
	// Modes of Operation
	//

	private final Map<Class<? extends Mode>, Mode> MODES = Stream.<Class<? extends Mode>>of(CellModeNormal.class, CellModeFillWithEssence.class, CellModeAutoMarrow.class)
		.collect(Collectors.toMap(Function.identity(), mode -> {
			final Mode instance = Classes.instantiate(mode);
			instance.cell = this;
			return instance;
		}));

	public boolean isModeActive (final Class<? extends Mode> mode) {
		return MODES.get(mode).isActive();
	}

	public static abstract class Mode {

		public ComposerCellTileEntity cell;

		public abstract String getName ();

		public abstract boolean isActive ();

		public boolean shouldAlwaysUpdate () {
			return false;
		}

		public boolean isLockingStoredItem () {
			return false;
		}

		public boolean tryInsert (final ItemStack stack, final int requestedQuantity) {
			return false;
		}

		public int getMaxContainedQuantityForOtherModes (final ItemStack stack) {
			return Integer.MAX_VALUE;
		}

		public boolean tryExtract (final List<ItemStack> extracted) {
			return false;
		}

		public void update () {
		}

		public void onWriteToNBT (final NBTTagCompound compound) {
		}

		public void onReadFromNBT (final NBTTagCompound compound) {
		}

		public void onWailaTooltipHeader (final List<String> currentTooltip, final EntityPlayer player) {
		}

		public void onWailaTooltipMore (final List<String> currentTooltip, final EntityPlayer player) {
		}

		public boolean allowRenderingItemInTooltip () {
			return true;
		}

		public boolean allowRenderingExtraItemDetailsInTooltip () {
			return true;
		}

		public double getSpinSpeed () {
			return 0;
		}
	}


	/////////////////////////////////////////
	// Utility
	//

	public static boolean areItemStacksEqual (ItemStack stackA, ItemStack stackB) {
		if (stackA.getItem() != stackB.getItem())
			return false;

		if (stackA.getItemDamage() != stackB.getItemDamage())
			return false;

		if (stackA.getTagCompound() == null && stackB.getTagCompound() != null)
			return false;

		return (stackA.getTagCompound() == null || stackA.getTagCompound()
			.equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
	}

	public List<EntityItem> getCaptureItems () {
		return world
			.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos)
				.offset(new BlockPos(EnumFacing.UP.getDirectionVec()))
				.contract(0, 0.8, 0), EntitySelectors.IS_ALIVE);
	}

	public static void addItemStackToList (final ItemStack item, final List<ItemStack> list, int quantity) {
		final int maxStackSize = item.getMaxStackSize();
		while (quantity > 0) {
			final int stackSize = Math.min(maxStackSize, quantity);
			final ItemStack stack = item.copy();
			stack.setCount(stackSize);
			list.add(stack);
			quantity -= maxStackSize;
		}
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT (final NBTTagCompound compound) {
		compound.setBoolean("has_composer", composerLocation != null);

		if (composerLocation != null) {
			compound.setInteger("composer_x", composerLocation.getX());
			compound.setInteger("composer_y", composerLocation.getY());
			compound.setInteger("composer_z", composerLocation.getZ());
		}

		compound.setInteger("stored_quantity", storedQuantity);

		if (storedQuantity > 0 && storedItem != null)
			compound.setTag("stored_item", storedItem.writeToNBT(new NBTTagCompound()));

		compound.setByte("slot", slot);

		for (final Mode mode : MODES.values())
			mode.onWriteToNBT(compound);
	}

	@Override
	public void onReadFromNBT (final NBTTagCompound compound) {
		if (compound.getBoolean("has_composer"))
			composerLocation = new BlockPos( //
				compound.getInteger("composer_x"), //
				compound.getInteger("composer_y"), //
				compound.getInteger("composer_z"));

		storedQuantity = compound.getInteger("stored_quantity");
		storedItem = storedQuantity <= 0 ? null : new ItemStack(compound.getCompoundTag("stored_item"));

		slot = compound.getByte("slot");

		for (final Mode mode : MODES.values())
			mode.onReadFromNBT(compound);
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

	@Override
	public double getSpinSpeed () {
		return MODES.values()
			.stream()
			.filter(mode -> mode.isActive())
			.map(mode -> mode.getSpinSpeed())
			.max(Double::compare)
			.orElse(0.0);
	}


	////////////////////////////////////
	// Waila
	//

	public void onWailaTooltipHeader (final List<String> currentTooltip, final EntityPlayer player) {
		currentTooltip.add(new Translation("waila." + Soulus.MODID + ":composer_cell.contained_item")
			.addArgs(storedQuantity, CONFIG.maxQuantity, storedItem.getDisplayName())
			.get());

		if (!player.isSneaking() && storedItem != null && storedItem.getItem() instanceof IHasComposerCellInfo && shouldShowItemInTooltip(false))
			((IHasComposerCellInfo) storedItem.getItem())
				.addComposerCellInfo(currentTooltip, storedItem, storedQuantity);

		for (final Mode mode : MODES.values())
			if (mode.isActive())
				mode.onWailaTooltipHeader(currentTooltip, player);
	}

	public static interface IHasComposerCellInfo {

		abstract void addComposerCellInfo (List<String> currentTooltip, ItemStack stack, int stackSize);
	}

	public void onWailaTooltipMore (final List<String> currentTooltip, final EntityPlayer player) {
		for (final Mode mode : MODES.values())
			if (mode.isActive())
				mode.onWailaTooltipMore(currentTooltip, player);
	}

	public boolean shouldShowItemInTooltip (final boolean isExtra) {
		return MODES.values()
			.stream()
			// all modes are either not active or allow rendering the item in tooltip
			.allMatch(mode -> !mode.isActive() //
				|| (mode.allowRenderingItemInTooltip() //
					&& (isExtra == false || mode.allowRenderingExtraItemDetailsInTooltip())));
	}
}
