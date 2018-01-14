package yuudaari.soulus.common.block.upgradeable_block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import yuudaari.soulus.Soulus;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public abstract class UpgradeableBlock<TileEntityClass extends UpgradeableBlockTileEntity> extends ModBlock {

	/////////////////////////////////////////
	// Upgrades
	//
	public static interface IUpgrade {

		public ItemStack getItemStack (int quantity);

		public default ItemStack getItemStackForTileEntity (UpgradeableBlockTileEntity te, int quantity) {
			return getItemStack(quantity);
		}

		public default void addItemStackToList (List<ItemStack> list, int quantity) {
			addItemStackToList(null, list, quantity);
		}

		public default void addItemStackToList (UpgradeableBlockTileEntity te, List<ItemStack> list, int quantity) {
			ItemStack item = te == null ? getItemStack(1) : getItemStackForTileEntity(te, 1);
			int maxStackSize = item.getMaxStackSize();
			while (quantity > 0) {
				int stackSize = Math.min(maxStackSize, quantity);
				list.add(te == null ? getItemStack(stackSize) : getItemStackForTileEntity(te, stackSize));
				quantity -= maxStackSize;
			}
		}

		public default boolean canOverrideMaxQuantity () {
			return true;
		}

		public boolean isItemStack (ItemStack stack);

		public int getIndex ();

		public String getName ();

		public int getMaxQuantity ();

		public void setMaxQuantity (int quantity);
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
					.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()), player.isCreative());
		}
	}

	@Override
	public final void onBlockExploded (World world, BlockPos pos, Explosion explosion) {
		onBlockDestroy(world, pos, 0, false);
	}

	public final void onBlockDestroy (World world, BlockPos pos, boolean creative) {
		onBlockDestroy(world, pos, 0, creative);
	}

	public void onBlockDestroy (World world, BlockPos pos, int fortune, boolean creative) {
		List<ItemStack> drops = getDropsForBreak(world, pos, world.getBlockState(pos), fortune, creative);

		dropItems(world, drops, pos);
	}

	@Override
	public final boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack heldStack = player.getHeldItem(hand);

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

		ute.insertUpgrade(stack, upgrade, player.isSneaking() ? stack.getCount() : 1);

		return true;
	}

	@Override
	public final void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}

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

	public void addOtherDropStacksToList (List<ItemStack> list, World world, BlockPos pos, IBlockState state) {}

	public void addBlockToList (List<ItemStack> list, World world, BlockPos pos) {
		list.add(getItemStack());
	}

	public IUpgrade isUpgradeItem (ItemStack stack, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return null;

		return ((UpgradeableBlockTileEntity) te).getUpgradeForItem(stack);
	}

	public boolean canActivateWithItem (ItemStack stack, World world, BlockPos pos) {
		return stack.isEmpty() || isUpgradeItem(stack, world, pos) != null;
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return true;
	}

	@Override
	public abstract UpgradeableBlockTileEntity createTileEntity (World world, IBlockState state);

	@Override
	public abstract Class<? extends UpgradeableBlockTileEntity> getTileEntityClass ();

	/////////////////////////////////////////
	// Waila
	//

	@Optional.Method(modid = "waila")
	@Override
	public ItemStack getWailaStack (IWailaDataAccessor accessor) {
		return getItemStack();
	}

	@SuppressWarnings("unchecked")
	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	@Override
	public final List<String> getWailaTooltip (List<String> currentTooltip, IWailaDataAccessor accessor) {
		TileEntity te = accessor.getTileEntity();
		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return currentTooltip;

		UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

		boolean isSneaking = accessor.getPlayer().isSneaking();

		onWailaTooltipHeader(currentTooltip, accessor.getBlockState(), (TileEntityClass) te, isSneaking);

		List<IUpgrade> upgrades = new ArrayList<>(Arrays.asList(getUpgrades()));

		if (isSneaking || upgrades.size() < 2) {
			for (IUpgrade upgrade : Lists.reverse(ute.insertionOrder)) {
				upgrades.remove(upgrade);
				currentTooltip
					.add(I18n.format("waila." + getRegistryName() + ".upgrades_" + upgrade.getName()
						.toLowerCase(), ute.upgrades.get(upgrade), upgrade.getMaxQuantity()));
			}
			for (IUpgrade upgrade : upgrades) {
				currentTooltip
					.add(I18n.format("waila." + getRegistryName() + ".upgrades_" + upgrade.getName()
						.toLowerCase(), ute.upgrades.get(upgrade), upgrade.getMaxQuantity()));
			}
		} else {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":upgradeable_block.show_upgrades"));
		}

		onWailaTooltipFooter(currentTooltip, accessor.getBlockState(), (TileEntityClass) te, isSneaking);

		return currentTooltip;
	}

	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, TileEntityClass te, boolean isSneaking) {}

	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	protected void onWailaTooltipFooter (List<String> currentTooltip, IBlockState blockState, TileEntityClass te, boolean isSneaking) {}

}
