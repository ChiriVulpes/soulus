package yuudaari.soulus.common.block;

import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.soul_totem.SoulTotem;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import java.util.List;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNiobium extends ModBlock {

	public static final PropertyBool HAS_COMPARATOR = PropertyBool.create("has_comparator");

	public BlockNiobium () {
		super("block_niobium", new Material(MapColor.BLUE));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		setTickRandomly(false);
		setDefaultState(super.getDefaultState().withProperty(HAS_COMPARATOR, false));
		setHasDescription();
	}

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			HAS_COMPARATOR
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(HAS_COMPARATOR, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(HAS_COMPARATOR) ? 1 : 0;
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride (IBlockState state, World world, BlockPos pos) {
		if (!state.getValue(HAS_COMPARATOR)) {
			world.setBlockState(pos, getDefaultState().withProperty(HAS_COMPARATOR, true), 7);
		}

		BlockNiobiumTileEntity te = (BlockNiobiumTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.power;
	}

	@Override
	public void addCollisionBoxToList (IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));

		/*
		 * The following code is to assure that when connected to a soul totem, the soul totem has collision detection on the corners
		 */
		IBlockState connectedTotem = ModBlocks.SOUL_TOTEM.getDefaultState().withProperty(SoulTotem.CONNECTED, true);

		BlockPos totemPos = pos.add(-1, 1, -1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			ModBlocks.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(1, 1, -1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			ModBlocks.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(-1, 1, 1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			ModBlocks.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(1, 1, 1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			ModBlocks.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return state.getValue(HAS_COMPARATOR);
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass () {
		return BlockNiobiumTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new BlockNiobiumTileEntity();
	}

	public static class BlockNiobiumTileEntity extends TileEntity implements ITickable {

		public int power = 0;

		@Override
		public void update () {
			if (hasComparator()) {

				int powerIn = world.isBlockIndirectlyGettingPowered(pos);
				int newPower = 15 - powerIn;

				if (this.power != newPower) {
					this.power = newPower;
					markDirty();
				}

			} else {
				world.setBlockState(pos, ModBlocks.BLOCK_NIOBIUM.getDefaultState()
					.withProperty(HAS_COMPARATOR, false), 7);
			}
		}

		private boolean hasComparator () {
			IBlockState b1 = world.getBlockState(offsetBlockPos(0, -1));
			IBlockState b2 = world.getBlockState(offsetBlockPos(0, -2));
			if (isComparatorCheckingMeOut(b1, b2, EnumFacing.SOUTH))
				return true;

			b1 = world.getBlockState(offsetBlockPos(1, 0));
			b2 = world.getBlockState(offsetBlockPos(2, 0));
			if (isComparatorCheckingMeOut(b1, b2, EnumFacing.WEST))
				return true;

			b1 = world.getBlockState(offsetBlockPos(0, 1));
			b2 = world.getBlockState(offsetBlockPos(0, 2));
			if (isComparatorCheckingMeOut(b1, b2, EnumFacing.NORTH))
				return true;

			b1 = world.getBlockState(offsetBlockPos(-1, 0));
			b2 = world.getBlockState(offsetBlockPos(-2, 0));
			if (isComparatorCheckingMeOut(b1, b2, EnumFacing.EAST))
				return true;

			return false;
		}

		private boolean isComparatorCheckingMeOut (IBlockState block1, IBlockState block2, EnumFacing facing) {
			if (isComparator(block1)) {
				return block1.getValue(BlockHorizontal.FACING) == facing;

			} else if (isComparator(block2)) {
				return !block1.isTranslucent() && !(block1.getBlock() instanceof BlockEndersteel) && block2
					.getValue(BlockHorizontal.FACING) == facing;
			}

			return false;
		}

		private boolean isComparator (IBlockState block) {
			return block.getBlock() instanceof BlockRedstoneComparator;
		}

		private BlockPos offsetBlockPos (int x, int z) {
			return new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
		}
	}
}
