package yuudaari.soulus.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlock;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlockTileEntity;
import yuudaari.soulus.common.util.Material;

public class BlockEndersteelDark extends ComparatorInteractiveBlock {

	public BlockEndersteelDark () {
		super("block_endersteel_dark", new Material(MapColor.BLACK));
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
	}

	@Override
	public Class<? extends ComparatorInteractiveBlockTileEntity> getTileEntityClass () {
		return BlockEndersteelDarkTileEntity.class;
	}

	@Override
	public ComparatorInteractiveBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new BlockEndersteelDarkTileEntity();
	}

	public static class BlockEndersteelDarkTileEntity extends ComparatorInteractiveBlockTileEntity {

		@Override
		protected boolean shouldCancelRefreshIfSameSignal () {
			return true;
		}

		@Override
		protected int getSignal (int signalIn) {
			return signalIn > 0 ? 1 + world.rand.nextInt(15) : 0;
		}

		@Override
		public void powerChange () {
			blockUpdate();
		}

		private final void blockUpdate () {
			if (world != null) {
				IBlockState blockState = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, blockState, blockState, 3);
				markDirty();
			}
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
