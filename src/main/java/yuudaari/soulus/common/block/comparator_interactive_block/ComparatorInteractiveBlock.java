package yuudaari.soulus.common.block.comparator_interactive_block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Material;

public abstract class ComparatorInteractiveBlock extends Registration.Block {

	public static final PropertyBool HAS_COMPARATOR = PropertyBool.create("has_comparator");

	public ComparatorInteractiveBlock (String id, Material material) {
		super(id, material);
		setHasItem();
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

		ComparatorInteractiveBlockTileEntity te = (ComparatorInteractiveBlockTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.signalOut;
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return state.getValue(HAS_COMPARATOR);
	}

	@Override
	public abstract Class<? extends ComparatorInteractiveBlockTileEntity> getTileEntityClass ();

	@Override
	public abstract ComparatorInteractiveBlockTileEntity createTileEntity (World worldIn, IBlockState blockState);
}
