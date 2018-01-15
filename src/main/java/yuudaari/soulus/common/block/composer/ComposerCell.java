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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
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

	@Inject(ConfigComposerCell.class) public static ConfigComposerCell CONFIG;

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

	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
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

	/*
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
	
		IBlockState state = getDefaultState().withProperty(FACING,
				EnumFacing.getDirectionFromEntityLiving(pos, placer));
	
		EnumFacing direction = validateStructure(world, pos);
		if (direction != null && state.getValue(Composer.FACING) != direction)
			state = state.withProperty(Composer.FACING, direction).withProperty(CONNECTED, true);
	
		return state;
	}
	*/

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

	@Override
	public UpgradeableBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new ComposerCellTileEntity();
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

		ItemStack currentStack = te.storedItem;
		if (currentStack == null || areItemStacksEqual(stack, currentStack)) {
			int requestedInsertQuantity = player.isSneaking() ? stack.getCount() : 1;
			int canStillBeInsertedQuantity = CONFIG.maxQuantity - (currentStack == null ? 0 : te.storedQuantity);
			int insertQuantity = Math.min(requestedInsertQuantity, canStillBeInsertedQuantity);

			if (currentStack == null) {
				te.storedItem = stack.copy();
				te.storedItem.setCount(1);
				te.storedQuantity = insertQuantity;
			} else {
				te.storedQuantity += insertQuantity;
			}

			stack.shrink(insertQuantity);
			te.onChangeItem();
			te.blockUpdate();

			return true;
		}

		return false;
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
	// Utility
	//

	public static boolean areItemStacksEqual (ItemStack stackA, ItemStack stackB) {
		if (stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		} else if (stackA.isEmpty() != stackB.isEmpty()) {
			return false;
		} else if (stackA.getItem() != stackB.getItem()) {
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

	/////////////////////////////////////////
	// Waila
	//

	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, ComposerCellTileEntity te, boolean isSneaking) {

		// currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer_cell.slot", te.slot));

		if (te.storedQuantity == 0) {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer_cell.no_items"));
		} else {
			currentTooltip.add(I18n
				.format("waila." + Soulus.MODID + ":composer_cell.contained_item", te.storedQuantity, CONFIG.maxQuantity, te.storedItem
					.getDisplayName()));
		}
	}
}
