package yuudaari.soulus.common.block;

import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEndersteelDark extends ModBlock {

	public static final PropertyBool HAS_COMPARATOR = PropertyBool.create("has_comparator");

	public BlockEndersteelDark () {
		super("block_endersteel_dark", new Material(MapColor.BLACK));
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

		BlockEndersteelDarkTileEntity te = (BlockEndersteelDarkTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.signalOut;
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return state.getValue(HAS_COMPARATOR);
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass () {
		return BlockEndersteelDarkTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new BlockEndersteelDarkTileEntity();
	}

	public static class BlockEndersteelDarkTileEntity extends TileEntity implements ITickable {

		public int signalOut = 0;
		public int signalIn = 0;

		@Override
		public void update () {
			if (hasComparator()) {
				int signalIn = world.isBlockIndirectlyGettingPowered(pos);

				if (signalIn != this.signalIn) {
					this.signalIn = signalIn;

					int signalOut = signalIn > 0 ? 1 + world.rand.nextInt(15) : 0;
					if (this.signalOut != signalOut) {
						this.signalOut = signalOut;
						blockUpdate();
					}
				}

			} else {
				world.setBlockState(pos, ModBlocks.BLOCK_ENDERSTEEL_DARK.getDefaultState()
					.withProperty(HAS_COMPARATOR, false), 7);
			}
		}

		private final void blockUpdate () {
			if (world != null) {
				IBlockState blockState = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, blockState, blockState, 3);
				markDirty();
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

		@Override
		public void readFromNBT (NBTTagCompound compound) {
			signalOut = compound.getByte("signalOut");
			signalIn = compound.getByte("signalIn");
			super.readFromNBT(compound);
		}

		@Override
		public NBTTagCompound writeToNBT (NBTTagCompound compound) {
			compound = super.writeToNBT(compound);
			compound.setByte("signalOut", (byte) signalOut);
			compound.setByte("signalIn", (byte) signalIn);
			return compound;
		}

		@Override
		public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return oldState.getBlock() != newState.getBlock() || !oldState.equals(newState);
		}
	}
}
