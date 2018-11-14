package yuudaari.soulus.common.block.comparator_interactive_block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public abstract class ComparatorInteractiveBlockTileEntity extends TileEntity implements ITickable {

	public int signalOut = 0;
	protected int signalIn = 0;
	private int refreshTicks = -1;

	protected abstract int getSignal (int signalIn);

	protected boolean shouldCancelRefreshIfSameSignal () {
		return false;
	}

	@Override
	public void update () {
		refreshTicks++;
		if (refreshTicks % 2 != 0) return;

		if (refreshTicks % 20 != 0 && !hasComparator()) {
			world.setBlockState(pos, this.blockType.getDefaultState()
				.withProperty(ComparatorInteractiveBlock.HAS_COMPARATOR, false), 7);
			return;
		}

		int signalIn = world.isBlockIndirectlyGettingPowered(pos);
		if (signalIn == this.signalIn && shouldCancelRefreshIfSameSignal()) return;

		this.signalIn = signalIn;

		int signalOut = getSignal(signalIn);

		if (this.signalOut != signalOut) {
			this.signalOut = signalOut;
			powerChange();
		}
	}

	public void powerChange () {
		markDirty();
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
			return !block1.isTranslucent() && !(block1.getBlock() instanceof ComparatorInteractiveBlock) && block2
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
