package yuudaari.soulus.common.block.composer;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposer;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.StructureMap;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;
import yuudaari.soulus.Soulus;

@ConfigInjected(Soulus.MODID)
public class Composer extends UpgradeableBlock<ComposerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		RANGE (0, "range", ModItems.ORB_MURKY.getItemStack()), //
		DELAY (1, "delay", ModItems.GEAR_OSCILLATING.getItemStack());

		private final int index;
		private final String name;
		private final ItemStack stack;
		// by default all upgrades are capped at 16
		private Integer maxQuantity;

		private Upgrade (int index, String name, ItemStack item) {
			this.index = index;
			this.name = name;
			this.stack = item;
		}

		@Override
		public int getIndex () {
			return index;
		}

		@Override
		public String getName () {
			return name;
		}

		@Override
		public int getMaxQuantity () {
			// all upgrades by default are capped at 16
			if (maxQuantity == null) {
				if (name == "range")
					return 16;
				if (name == "delay")
					return 16;
			}

			return maxQuantity;
		}

		@Override
		public void setMaxQuantity (int quantity) {
			maxQuantity = quantity;
		}

		@Override
		public boolean isItemStack (ItemStack stack) {
			if (stack.getItem() != this.stack.getItem())
				return false;

			if (name == "range")
				return OrbMurky.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = new ItemStack(this.stack.getItem(), quantity);

			if (name == "range")
				OrbMurky.setFilled(stack);

			return stack;
		}
	}

	@Override
	public IUpgrade[] getUpgrades () {
		return Upgrade.values();
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigComposer CONFIG;

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public Composer () {
		super("composer", new Material(MapColor.GRASS).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(CONNECTED, false));
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<ComposerTileEntity> getInstance () {
		return ModBlocks.COMPOSER;
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		face = rotateFace(state.getValue(FACING), face);

		return Arrays.asList(EnumFacing.HORIZONTALS).contains(face) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
	}

	public EnumFacing rotateFace (EnumFacing rotateFace, EnumFacing face) {
		if (rotateFace == EnumFacing.SOUTH)
			return face.getOpposite();
		else if (rotateFace == EnumFacing.EAST)
			return face == EnumFacing.UP || face == EnumFacing.DOWN ? face : face.rotateY();
		else if (rotateFace == EnumFacing.WEST)
			return face == EnumFacing.UP || face == EnumFacing.DOWN ? face : face.rotateYCCW();
		else if (rotateFace == EnumFacing.UP)
			return face.rotateAround(Axis.X);
		else if (rotateFace == EnumFacing.DOWN)
			return face.rotateAround(Axis.X).getOpposite();

		return face;
	}

	@Override
	public void onBlockDestroy (World world, BlockPos pos, int fortune, boolean creative) {
		super.onBlockDestroy(world, pos, fortune, creative);

		IBlockState state = world.getBlockState(pos);

		if (state.getValue(CONNECTED)) {

			structure.loopBlocks(world, pos, state.getValue(FACING), (BlockPos pos2, BlockValidator validator) -> {
				IBlockState currentState = world.getBlockState(pos2);

				if (currentState.getBlock() == ModBlocks.COMPOSER_CELL) {
					world.setBlockState(pos2, currentState
						.withProperty(ComposerCell.CELL_STATE, ComposerCell.CellState.DISCONNECTED), 3);

					ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(pos2);
					ccte.composerLocation = null;
					ccte.blockUpdate();
				}

				return null;
			});

		}
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride (IBlockState state, World world, BlockPos pos) {
		ComposerTileEntity te = (ComposerTileEntity) world.getTileEntity(pos);
		return te.getSignalStrength();
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			FACING, CONNECTED
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(CONNECTED, (meta & 1) == 0 ? false : true)
			.withProperty(FACING, EnumFacing.getFront(meta / 2));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(FACING).getIndex() * 2 + (state.getValue(CONNECTED) ? 1 : 0);
	}

	public IBlockState withRotation (IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror (IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

		EnumFacing defaultDirection = EnumFacing.getDirectionFromEntityLiving(pos, placer);
		IBlockState state = getDefaultState().withProperty(FACING, defaultDirection);

		EnumFacing direction = validateStructure(world, pos, defaultDirection);
		if (direction != null && state.getValue(Composer.FACING) != direction)
			state = state.withProperty(Composer.FACING, direction);

		return state;
	}

	/////////////////////////////////////////
	// Structure
	//

	public StructureMap structure = new StructureMap();
	{
		BlockValidator bars = BlockValidator.byBlock(ModBlocks.BARS_ENDERSTEEL);
		BlockValidator cell = (pos, world, checkPos, state) -> {
			if (state.getBlock() != ModBlocks.COMPOSER_CELL)
				return false;
			ComposerCellTileEntity te = (ComposerCellTileEntity) world.getTileEntity(checkPos);
			boolean result = te == null || (te.composerLocation == null && te.changeComposerCooldown < 0) || pos
				.equals(te.composerLocation);
			return result;
		};
		BlockValidator obsidian = BlockValidator.byBlock(Blocks.OBSIDIAN);
		BlockValidator endersteel = BlockValidator.byBlock(ModBlocks.BLOCK_ENDERSTEEL);

		// layer 1
		structure.addBlock(-2, 0, -5, obsidian);
		structure.addRowX(-1, 0, -5, 3, bars);
		structure.addBlock(2, 0, -5, obsidian);

		structure.addBlock(-2, 0, -4, bars);
		structure.addRowX(-1, 0, -4, 3, cell);
		structure.addBlock(2, 0, -4, bars);

		structure.addBlock(-2, 0, -3, bars);
		structure.addRowX(-1, 0, -3, 3, cell);
		structure.addBlock(2, 0, -3, bars);

		structure.addBlock(-2, 0, -2, bars);
		structure.addRowX(-1, 0, -2, 3, cell);
		structure.addBlock(2, 0, -2, bars);

		structure.addBlock(-2, 0, -1, obsidian);
		structure.addRowX(-1, 0, -1, 3, bars);
		structure.addBlock(2, 0, -1, obsidian);

		// layer 2
		structure.addBlock(-2, 1, -5, obsidian);
		structure.addBlock(-1, 1, -5, bars);
		structure.addBlock(1, 1, -5, bars);
		structure.addBlock(2, 1, -5, obsidian);

		structure.addBlock(-2, 1, -4, bars);
		structure.addBlock(2, 1, -4, bars);

		structure.addBlock(-2, 1, -2, bars);
		structure.addBlock(2, 1, -2, bars);

		structure.addBlock(-2, 1, -1, obsidian);
		structure.addBlock(-1, 1, -1, bars);
		structure.addBlock(1, 1, -1, bars);
		structure.addBlock(2, 1, -1, obsidian);

		// layer 3
		structure.addBlock(-2, 2, -5, endersteel);
		structure.addBlock(2, 2, -5, endersteel);

		structure.addBlock(-2, 2, -1, endersteel);
		structure.addBlock(2, 2, -1, endersteel);
	}

	public EnumFacing validateStructure (World world, BlockPos pos, EnumFacing currentDirection) {

		boolean checkCurrentDirection = currentDirection != EnumFacing.DOWN && currentDirection != EnumFacing.UP;

		if (checkCurrentDirection && structure.isValid(world, pos, currentDirection))
			return currentDirection;

		else {
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				if (checkCurrentDirection && facing == currentDirection)
					continue;
				if (structure.isValid(world, pos, facing))
					return facing;
			}
		}

		return null;
		// return structure.isValid(world, pos, world.getBlockState(pos).getValue(FACING));
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
		return ComposerTileEntity.class;
	}

	@Override
	public UpgradeableBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new ComposerTileEntity();
	}

	/////////////////////////////////////////
	// Waila
	//

	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, ComposerTileEntity te, boolean isSneaking) {

		if (te.hasValidRecipe()) {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer.activation", (int) Math
				.floor(te.activationAmount())));
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer.craft_percentage", (int) Math
				.floor(te.getCompositionPercent() * 100)));
		} else {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":composer.no_recipe"));
		}

	}
}
