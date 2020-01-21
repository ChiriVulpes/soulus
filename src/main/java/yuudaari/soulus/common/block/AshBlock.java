package yuudaari.soulus.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.registration.Registration;

public class AshBlock extends Registration.Block {

	protected static final AxisAlignedBB ASH_AABB = new AxisAlignedBB(0.20000001192092896D, 0.0D, 0.20000001192092896D, 0.799999988079071D, 0.31250001192092896D, 0.799999988079071D);

	public AshBlock () {
		super("ash_block", new MaterialTransparent(MapColor.BLACK).setNoPushMobility());
		setSoundType(SoundType.PLANT);
		registerWailaProvider(AshBlock.class);
	}

	@Override
	public CreativeTab getCreativeTabToDisplayOn () {
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
		return ASH_AABB.offset(state.getOffset(source, pos));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox (IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public EnumOffsetType getOffsetType () {
		return EnumOffsetType.XZ;
	}

	@Override
	public void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(ItemRegistry.ASH.getItemStack(2));
	}

	@Override
	public void neighborChanged (IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.getBlockState(pos.down()).isFullBlock())
			world.destroyBlock(pos, true);
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}

	@Override
	public ItemStack getWailaStack (IDataAccessor accessor) {
		return ItemRegistry.ASH.getItemStack();
	}
}
