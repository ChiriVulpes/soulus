package yuudaari.soulus.common.block;

import mcp.mobius.waila.api.IWailaDataAccessor;
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
import net.minecraftforge.fml.common.Optional;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.ModBlock;

public class AshBlock extends ModBlock {

	protected static final AxisAlignedBB ASH_AABB = new AxisAlignedBB(0.20000001192092896D, 0.0D, 0.20000001192092896D, 0.799999988079071D, 0.31250001192092896D, 0.799999988079071D);

	public AshBlock () {
		super("ash_block", new MaterialTransparent(MapColor.BLACK).setReplaceable());
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
		drops.add(ModItems.ASH.getItemStack(2));
	}

	@Override
	public void neighborChanged (IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.getBlockState(pos.down()).isFullBlock()) {
			world.destroyBlock(pos, true);
		}
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

	@Optional.Method(modid = "waila")
	@Override
	public ItemStack getWailaStack (IWailaDataAccessor accessor) {
		return ModItems.ASH.getItemStack();
	}
}
