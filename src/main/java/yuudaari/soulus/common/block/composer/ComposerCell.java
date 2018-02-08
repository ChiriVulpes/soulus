package yuudaari.soulus.common.block.composer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.Soulus;

@ConfigInjected(Soulus.MODID)
public class ComposerCell extends UpgradeableBlock<ComposerCellTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	@Override
	public IUpgrade[] getUpgrades () {
		return new IUpgrade[0];
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigComposerCell CONFIG;

	/////////////////////////////////////////
	// Properties
	//

	public static enum CellState implements IStringSerializable {
		DISCONNECTED ("disconnected"),
		CONNECTED_CENTER ("connected_center"),
		CONNECTED_EDGE ("connected_edge");

		public String name;

		CellState (String name) {
			this.name = name;
		}

		@Override
		public String getName () {
			return name;
		}

		public static CellState fromMeta (int meta) {
			return values()[meta];
		}

		public int getMeta () {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].equals(this))
					return i;
			}
			throw new RuntimeException("Can't find own index");
		}
	}

	public static final IProperty<CellState> CELL_STATE = PropertyEnum.create("cell_state", CellState.class);

	public ComposerCell () {
		super("composer_cell", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setDefaultState(getDefaultState().withProperty(CELL_STATE, CellState.DISCONNECTED));
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<ComposerCellTileEntity> getInstance () {
		return ModBlocks.COMPOSER_CELL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}

	@Override
	public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if (state.getValue(CELL_STATE) == CellState.DISCONNECTED) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(.0625, 0, .0625, .9375, .25, .9375));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(.1875, .25, .1875, .9375, .5625, .8125));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, .5625, 0, 1, 1, 1));

		} else {
			// if it's part of a structure, the shape doesn't matter, it should just be cubes
			addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));
		}
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride (IBlockState blockState, World world, BlockPos pos) {
		ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(pos);
		ItemStack stack = te.getStoredItem();
		if (stack == null || te.storedQuantity <= 0) return 0;

		Item item = stack.getItem();
		if (item instanceof IFillableWithEssence && te.storedQuantity == 1) {
			return 1 + (int) Math.floor(14F * ((IFillableWithEssence) item).getFillPercentage(stack));
		}

		return 1 + (int) Math.floor(14 * (te.storedQuantity / (float) CONFIG.maxQuantity));
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			CELL_STATE
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(CELL_STATE, CellState.values()[meta]);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(CELL_STATE).getMeta();
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity (IBlockState blockState) {
		return true;
	}

	@Override
	public Class<? extends UpgradeableBlockTileEntity> getTileEntityClass () {
		return ComposerCellTileEntity.class;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public boolean canActivateWithItem (ItemStack stack, World world, BlockPos pos) {
		ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(pos);
		return te.storedQuantity < CONFIG.maxQuantity;
	}

	@Override
	public boolean onActivateInsert (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(pos);

		return te.tryInsert(stack, player.isSneaking() ? stack.getCount() : 1);
	}

	@Override
	public boolean onActivateEmptyHand (World world, BlockPos pos, EntityPlayer player) {
		ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(pos);

		if (te.storedItem == null) {
			return false;
		}

		List<ItemStack> toReturn = new ArrayList<>();
		addItemStackToList(te.storedItem, toReturn, te.storedQuantity);

		returnItemsToPlayer(world, toReturn, player);
		te.storedItem = null;
		te.storedQuantity = 0;

		te.onChangeItem();
		te.blockUpdate();

		return true;
	}

	@Override
	public boolean onActivateEmptyHandSneaking (World world, BlockPos pos, EntityPlayer player) {
		return onActivateEmptyHand(world, pos, player);
	}

	public void addItemStackToList (ItemStack item, List<ItemStack> list, int quantity) {
		int maxStackSize = item.getMaxStackSize();
		while (quantity > 0) {
			int stackSize = Math.min(maxStackSize, quantity);
			ItemStack stack = item.copy();
			stack.setCount(stackSize);
			list.add(stack);
			quantity -= maxStackSize;
		}
	}

	@Override
	public void addOtherDropStacksToList (List<ItemStack> list, World world, BlockPos pos, IBlockState state) {
		ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(pos);

		if (te.storedItem == null)
			return;

		addItemStackToList(te.storedItem, list, te.storedQuantity);
	}

	/////////////////////////////////////////
	// Waila
	//

	@SideOnly(Side.CLIENT)
	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, ComposerCellTileEntity te, EntityPlayer player) {

		// currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer_cell.slot", te.slot));

		if (te.storedQuantity == 0) {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer_cell.no_items"));
		} else {
			currentTooltip.add(I18n
				.format("waila." + Soulus.MODID + ":composer_cell.contained_item", te.storedQuantity, CONFIG.maxQuantity, te.storedItem
					.getDisplayName()));

			ItemStack storedItem = te.getStoredItem();
			if (!player.isSneaking() && storedItem != null && storedItem.getItem() instanceof IHasImportantInfos) {
				((IHasImportantInfos) storedItem.getItem()).addImportantInformation(currentTooltip, storedItem);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected List<String> onWailaTooltipMore (IBlockState blockState, ComposerCellTileEntity te, EntityPlayer player) {
		ItemStack storedItem = te.getStoredItem();
		if (storedItem != null) {
			return storedItem.getTooltip(player, TooltipFlags.ADVANCED);
		}
		return null;
	}

	public static interface IHasImportantInfos {

		abstract void addImportantInformation (List<String> currentTooltip, ItemStack stack);
	}
}
