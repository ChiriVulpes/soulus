package yuudaari.soulus.common.block.upgradeable_block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import scala.Tuple3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public abstract class UpgradeableBlock<TileEntityClass extends UpgradeableBlockTileEntity> extends ModBlock {

	/////////////////////////////////////////
	// Upgrades
	//
	public static interface IUpgrade {

		public int getIndex ();

		public String getName ();

		public ItemStack getItemStack (int quantity);

		@Nullable
		public default ItemStack getItemStackForTileEntity (UpgradeableBlockTileEntity te, int quantity) {
			return getItemStack(quantity);
		}

		public default void addItemStackToList (List<ItemStack> list, int quantity) {
			addItemStackToList(null, list, quantity);
		}

		public default void addItemStackToList (UpgradeableBlockTileEntity te, List<ItemStack> list, int quantity) {
			ItemStack item = te == null ? getItemStack(1) : getItemStackForTileEntity(te, 1);
			if (item == null) return;
			int maxStackSize = item.getMaxStackSize();
			while (quantity > 0) {
				int stackSize = Math.min(maxStackSize, quantity);
				list.add(te == null ? getItemStack(stackSize) : getItemStackForTileEntity(te, stackSize));
				quantity -= maxStackSize;
			}
		}

		public boolean isItemStack (ItemStack stack);

		public default boolean isItemStackForTileEntity (ItemStack stack, UpgradeableBlockTileEntity te) {
			return isItemStack(stack);
		}

		public int getMaxQuantity ();

		public void setMaxQuantity (int quantity);

		public default boolean canOverrideMaxQuantity () {
			return true;
		}

		public default boolean isSecret () {
			return false;
		}
	}

	public abstract IUpgrade[] getUpgrades ();

	/////////////////////////////////////////
	// Constructor
	//

	public UpgradeableBlock (String name, Material material) {
		super(name, material);
		setHasItem();
		disableStats();
		registerWailaProvider(UpgradeableBlock.class);
	}

	public abstract UpgradeableBlock<TileEntityClass> getInstance ();

	/////////////////////////////////////////
	// Events
	//

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static final void rightClickBlock (PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof UpgradeableBlock) {
			UpgradeableBlock<? extends UpgradeableBlockTileEntity> ublock = (UpgradeableBlock<? extends UpgradeableBlockTileEntity>) block;
			if (ublock.canActivateWithItem(event.getItemStack(), world, pos)) {
				event.setUseBlock(Result.ALLOW);
			}
		}

	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static final void onBlockBreak (BlockEvent.BreakEvent event) {
		Block block = event.getState().getBlock();
		if (block instanceof UpgradeableBlock) {
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			EntityPlayer player = event.getPlayer();
			((UpgradeableBlock<? extends UpgradeableBlockTileEntity>) block)
				.onBlockDestroy(world, pos, EnchantmentHelper
					.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()), player);
		}
	}

	@Override
	public final void onBlockExploded (World world, BlockPos pos, Explosion explosion) {
		onBlockDestroy(world, pos, 0, null);
	}

	public final void onBlockDestroy (World world, BlockPos pos, @Nullable EntityPlayer player) {
		onBlockDestroy(world, pos, 0, player);
	}

	public void onBlockDestroy (World world, BlockPos pos, int fortune, @Nullable EntityPlayer player) {
		List<ItemStack> drops = getDropsForBreak(world, pos, world.getBlockState(pos), fortune, player.isCreative());

		dropItems(world, drops, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack heldStack = player.getHeldItem(hand);

			if (!canActivateTileEntity((TileEntityClass) world.getTileEntity(pos))) return false;

			if (heldStack.isEmpty()) {
				if (player.isSneaking()) {
					return onActivateEmptyHandSneaking(world, pos, player);
				} else {
					return onActivateEmptyHand(world, pos, player);
				}
			} else {
				return onActivateInsert(world, pos, player, heldStack);
			}
		}

		return true;
	}

	public boolean onActivateEmptyHandSneaking (World world, BlockPos pos, EntityPlayer player) {
		List<ItemStack> drops = getDropsForEmpty(world, pos, world.getBlockState(pos));

		returnItemsToPlayer(world, drops, player);

		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof UpgradeableBlockTileEntity) {
			UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

			ute.clear();
		}

		return true;
	}

	public boolean onActivateEmptyHand (World world, BlockPos pos, EntityPlayer player) {
		return onActivateReturnLastUpgrade(world, pos, player);
	}

	public final boolean onActivateReturnLastUpgrade (World world, BlockPos pos, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return false;

		UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

		IUpgrade upgrade = ute.popLastUpgrade();

		if (upgrade == null)
			return false;

		int count = ute.removeUpgrade(upgrade);

		List<ItemStack> toReturn = new ArrayList<>();
		upgrade.addItemStackToList(ute, toReturn, count);

		returnItemsToPlayer(world, toReturn, player);

		return true;
	}

	public boolean onActivateInsert (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		return onActivateInsertUpgrade(world, pos, player, stack);
	}

	public final boolean onActivateInsertUpgrade (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return false;

		UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

		IUpgrade upgrade = ute.getUpgradeForItem(stack);
		if (upgrade == null)
			return false;

		int insertQuantity = player.isSneaking() ? stack.getCount() : 1;
		ute.insertUpgrade(player.isCreative() ? stack.copy() : stack, upgrade, insertQuantity);

		boolean isFilled = ute.upgrades.get(upgrade) == upgrade.getMaxQuantity();
		Advancements.UPGRADE.trigger(player, new Tuple3<>(this, upgrade, isFilled));

		return true;
	}

	@Override
	public final void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	}

	/////////////////////////////////////////
	// Utility
	//

	protected static void returnItemsToPlayer (World world, List<ItemStack> items, EntityPlayer player) {
		for (ItemStack item : items) {
			EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, item);
			dropItem.setNoPickupDelay();
			world.spawnEntity(dropItem);
		}
	}

	protected static void dropItems (World world, List<ItemStack> items, BlockPos pos) {
		for (ItemStack item : items) {
			EntityItem dropItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
			dropItem.setNoPickupDelay();
			world.spawnEntity(dropItem);
		}
	}

	public final List<ItemStack> getDropsForBreak (World world, BlockPos pos, IBlockState state, int fortune, boolean creative) {
		List<ItemStack> result = new ArrayList<>();

		if (!creative)
			addBlockToList(result, world, pos);
		addUpgradeStacksToList(result, world, pos, state);
		addOtherDropStacksToList(result, world, pos, state);

		return result;
	}

	public List<ItemStack> getDropsForEmpty (World world, BlockPos pos, IBlockState state) {
		List<ItemStack> result = new ArrayList<>();

		addUpgradeStacksToList(result, world, pos, state);
		addOtherDropStacksToList(result, world, pos, state);

		return result;
	}

	public final void addUpgradeStacksToList (List<ItemStack> list, World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof UpgradeableBlockTileEntity) {
			((UpgradeableBlockTileEntity) te).addUpgradeStacksToList(list);
		}
	}

	public void addOtherDropStacksToList (List<ItemStack> list, World world, BlockPos pos, IBlockState state) {
	}

	public void addBlockToList (List<ItemStack> list, World world, BlockPos pos) {
		list.add(getItemStack());
	}

	public IUpgrade isUpgradeItem (ItemStack stack, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return null;

		return ((UpgradeableBlockTileEntity) te).getUpgradeForItem(stack);
	}

	@SuppressWarnings("unchecked")
	public final boolean canActivateWithItem (ItemStack stack, World world, BlockPos pos) {
		return canActivateTileEntity((TileEntityClass) world.getTileEntity(pos)) && canActivateWithStack(stack, world, pos);
	}

	public boolean canActivateWithStack (ItemStack stack, World world, BlockPos pos) {
		return stack.isEmpty() || isUpgradeItem(stack, world, pos) != null;
	}

	public boolean canActivateTileEntity (TileEntityClass te) {
		return true;
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return true;
	}

	@Override
	public UpgradeableBlockTileEntity createTileEntity (World world, IBlockState state) {
		try {
			return getTileEntityClass().newInstance();
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public abstract Class<? extends UpgradeableBlockTileEntity> getTileEntityClass ();

	/////////////////////////////////////////
	// Waila
	//

	@Override
	public ItemStack getWailaStack (IDataAccessor accessor) {
		return getItemStack();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<String> getWailaTooltip (List<String> currentTooltip, IDataAccessor accessor) {
		TileEntity te = accessor.getTileEntity();

		EntityPlayer player = accessor.getPlayer();

		IBlockState blockState = accessor.getBlockState();
		TileEntityClass ute = (TileEntityClass) te;

		onWailaTooltipHeader(currentTooltip, blockState, ute, player);

		onWailaTooltipBody(currentTooltip, blockState, ute, player);

		onWailaTooltipFooter(currentTooltip, blockState, ute, player);

		return currentTooltip;
	}

	protected boolean shouldWailaTooltipShowAll (IBlockState blockState, TileEntityClass te) {
		return false;
	}

	private final void onWailaTooltipBody (List<String> currentTooltip, IBlockState blockState, TileEntityClass te, EntityPlayer player) {
		List<String> upgrades = onWailaTooltipShowUpgrades(te);
		List<String> more = onWailaTooltipMore(blockState, te, player);
		int moreSize = more == null ? 0 : more.size();

		if (player.isSneaking() || upgrades.size() + moreSize < 2 || shouldWailaTooltipShowAll(blockState, te)) {
			currentTooltip.addAll(upgrades);
			if (more != null) currentTooltip.addAll(more);
		} else if (upgrades.size() + moreSize > 0) {
			if (moreSize > 0) {
				currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":upgradeable_block.show_more"));
			} else {
				currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":upgradeable_block.show_upgrades"));
			}
		}
	}

	private final List<String> onWailaTooltipShowUpgrades (TileEntityClass te) {
		List<String> tooltip = new ArrayList<>();

		if (te != null && te instanceof UpgradeableBlockTileEntity) {
			onWailaTooltipUpgrades(tooltip, te);
		}

		return tooltip;
	}

	protected void onWailaTooltipUpgrades (List<String> currentTooltip, TileEntityClass te) {
		List<IUpgrade> upgrades = new ArrayList<>(Arrays.asList(getUpgrades()));
		for (IUpgrade upgrade : Lists.reverse(te.insertionOrder)) {
			upgrades.remove(upgrade);
			String tooltip = getWailaTooltipUpgrade(upgrade, te);
			if (tooltip != null) currentTooltip.add(tooltip);
		}
		for (IUpgrade upgrade : upgrades) {
			String tooltip = getWailaTooltipUpgrade(upgrade, te);
			if (tooltip != null) currentTooltip.add(tooltip);
		}
	}

	@Nullable
	protected String getWailaTooltipUpgrade (IUpgrade upgrade, TileEntityClass te) {
		String upgradeName = upgrade.getName().toLowerCase();
		int upgradeCount = te.upgrades.get(upgrade);

		if (upgrade.isSecret() && upgradeCount == 0) return null;

		return Translation.localize("waila." + getRegistryName() + ".upgrades_" + upgradeName, //
			upgradeCount, upgrade.getMaxQuantity());
	}

	@Nullable
	protected List<String> onWailaTooltipMore (IBlockState blockState, TileEntityClass te, EntityPlayer player) {
		return null;
	}

	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, TileEntityClass te, EntityPlayer player) {
	}

	protected void onWailaTooltipFooter (List<String> currentTooltip, IBlockState blockState, TileEntityClass te, EntityPlayer player) {
	}

}
