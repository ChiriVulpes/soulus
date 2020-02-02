package yuudaari.soulus.common.block.composer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeAutoHammer;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeAutoMarrow;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeFillWithEssence;
import yuudaari.soulus.common.block.composer.cell_mode.CellModeNormal;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.ComposerCellItemParticles;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.Classes;
import yuudaari.soulus.common.util.ItemStackMutable;
import yuudaari.soulus.common.util.Translation;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

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


	////////////////////////////////////
	// Owner
	//

	private @Nullable UUID owner;

	public void setOwner (EntityPlayer owner) {
		this.owner = owner.getUniqueID();
	}

	public @Nullable EntityPlayer getOwner () {
		return owner == null ? null : world.getPlayerEntityByUUID(owner);
	}


	////////////////////////////////////
	// Main
	//

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
			if (tryInsert(new ItemStackMutable(stack), stack.getCount(), true))
				return true;
		}

		return false;
	}

	public boolean tryInsert (final ItemStackMutable stack, final int requestedQuantity, final boolean isPulling) {
		if (stack == null || stack.isEmpty())
			return false;

		if (storedItem != null && storedItem.isEmpty())
			storedItem = null;

		// insert the item!
		for (final Mode mode : MODES.values()) {
			if (!mode.isActive())
				continue;

			final int maxQuantityAllowed = MODES.values()
				.stream()
				.filter(m -> m != mode && m.isActive())
				.map(m -> m.getMaxContainedQuantityForOtherModes(stack.getImmutable()))
				.min(Integer::compare)
				.orElse(Integer.MAX_VALUE);

			final int maxInsertAllowed = maxQuantityAllowed - storedQuantity;

			if (mode.tryInsert(stack, Math.min(requestedQuantity, maxInsertAllowed), isPulling))
				return true;
		}

		return false;
	}

	public boolean tryExtract (final List<ItemStack> extracted) {
		if (!isStoredItemLockedInside() && storedItem != null && storedQuantity > 0) {
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

	private final Map<Class<? extends Mode>, Mode> MODES = Stream.<Class<? extends Mode>>of(CellModeNormal.class, CellModeFillWithEssence.class, CellModeAutoMarrow.class, CellModeAutoHammer.class)
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

		public boolean tryInsert (final ItemStackMutable stack, final int requestedQuantity, final boolean isPulling) {
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

		public double getSwingSpeed () {
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

	/**
	 * Note: The quantity added to the list is <code>quantity * item.getCount()</code>
	 */
	public static void addItemStackToList (final ItemStack item, final List<ItemStack> list, int quantity) {
		quantity = quantity * item.getCount();
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

		if (owner != null)
			compound.setString("owner", owner.toString());
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

		final String ownerString = compound.getString("owner");
		owner = ownerString.equals("") ? null : UUID.fromString(ownerString);
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
			.filter(Mode::isActive)
			.map(Mode::getSpinSpeed)
			.max(Double::compare)
			.orElse(0.0);
	}

	@Override
	public double getSwingSpeed () {
		return MODES.values()
			.stream()
			.filter(Mode::isActive)
			.map(Mode::getSwingSpeed)
			.max(Double::compare)
			.orElse(0.0);
	}

	public static void itemParticles (final World world, final BlockPos pos, final int boneChunk, final int count) {
		if (world.isRemote)
			itemParticles(world, pos, boneChunk, count, true);
		else
			SoulsPacketHandler.INSTANCE
				.sendToAllAround(new ComposerCellItemParticles(pos, boneChunk, count), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
	}

	@SideOnly(Side.CLIENT)
	private static void itemParticles (final World world, final BlockPos pos, final int chunk, final int count, final boolean clientside) {
		final double particleCount = Math.min(CONFIG.particleCount * count, CONFIG.particleCountMax);
		for (int i = 0; i < particleCount; ++i) {
			final double x = pos.getX() + 0.5;
			final double y = pos.getY() + 1.2;
			final double z = pos.getZ() + 0.5;

			final double vx = (Math.random() - 0.5) * 0.3;
			final double vy = Math.random() * 0.15;
			final double vz = (Math.random() - 0.5) * 0.3;

			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, vx, vy, vz, chunk);
		}
	}


	////////////////////////////////////
	// Tooltip Events
	//

	public void onWailaTooltipHeader (final List<String> currentTooltip, final EntityPlayer player) {
		// currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":composer_cell.slot", slot));

		if (storedItem == null || storedQuantity == 0)
			currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":composer_cell.no_items"));

		else if (shouldShowItemInTooltip(false)) {
			currentTooltip.add(new Translation("waila." + Soulus.MODID + ":composer_cell.contained_item")
				.addArgs(storedQuantity, CONFIG.maxQuantity, storedItem.getDisplayName())
				.get());

			if (!player.isSneaking() && storedItem.getItem() instanceof IHasComposerCellInfo)
				((IHasComposerCellInfo) storedItem.getItem())
					.addComposerCellInfo(currentTooltip, storedItem, storedQuantity);
		}

		for (final Mode mode : MODES.values())
			if (mode.isActive())
				mode.onWailaTooltipHeader(currentTooltip, player);
	}

	public void onWailaTooltipMore (final List<String> currentTooltip, final EntityPlayer player) {
		if (storedItem != null && shouldShowItemInTooltip(true))
			currentTooltip.addAll(cellProxy.getStackTooltip(storedItem, player));

		for (final Mode mode : MODES.values())
			if (mode.isActive())
				mode.onWailaTooltipMore(currentTooltip, player);
	}

	////////////////////////////////////
	// Tooltip Util
	//

	public boolean shouldShowItemInTooltip (final boolean isExtra) {
		return MODES.values()
			.stream()
			// all modes are either not active or allow rendering the item in tooltip
			.allMatch(mode -> !mode.isActive() //
				|| (mode.allowRenderingItemInTooltip() //
					&& (isExtra == false || mode.allowRenderingExtraItemDetailsInTooltip())));
	}

	@SidedProxy(modId = Soulus.MODID, serverSide = "yuudaari.soulus.common.block.composer.ComposerCellTileEntity$CommonProxy", clientSide = "yuudaari.soulus.common.block.composer.ComposerCellTileEntity$ClientProxy") //
	public static CommonProxy cellProxy;

	@SideOnly(Side.CLIENT)
	public static class ClientProxy extends CommonProxy {

		@Override
		public List<String> getStackTooltip (final ItemStack stack, final EntityPlayer player) {
			return stack.getTooltip(player, TooltipFlags.ADVANCED)
				.stream()
				.map(tooltipLine -> tooltipLine.length() > 0 ? "   " + tooltipLine : tooltipLine)
				.collect(Collectors.toList());
		}
	}

	public static class CommonProxy {

		public List<String> getStackTooltip (final ItemStack stack, final EntityPlayer player) {
			return new ArrayList<>();
		}
	}

	public static interface IHasComposerCellInfo {

		abstract void addComposerCellInfo (List<String> currentTooltip, ItemStack stack, int stackSize);
	}
}
