package yuudaari.soulus.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.misc.ModDamageSource;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;

public class Skewer extends ModBlock {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool EXTENDED = PropertyBool.create("extended");

	public Skewer() {
		super("skewer", new Material(MapColor.GRAY));
		setHasItem();
		setDefaultState(getDefaultState().withProperty(EXTENDED, false).withProperty(FACING, EnumFacing.NORTH));
		setHarvestLevel("pickaxe", 1);
		setHardness(3F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty<?>[] { FACING, EXTENDED });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(EXTENDED, (meta & 1) == 0 ? true : false).withProperty(FACING,
				EnumFacing.getFront(meta / 2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() * 2 + (state.getValue(EXTENDED) ? 1 : 0);
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		int power = world.isBlockIndirectlyGettingPowered(pos);
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
				.withProperty(EXTENDED, power > 0);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		int power = world.isBlockIndirectlyGettingPowered(pos);

		if (state.getValue(EXTENDED) != (power > 0)) {
			EnumFacing facing = state.getValue(FACING);

			if (power > 0) {
				BlockPos spikePos = pos.offset(facing);
				Block blockAtSpikePos = world.getBlockState(spikePos).getBlock();

				if (!blockAtSpikePos.equals(Blocks.AIR)) {
					if (!blockAtSpikePos.isReplaceable(world, spikePos))
						return;
				}
			}

			world.setBlockState(pos, getDefaultState().withProperty(FACING, facing).withProperty(EXTENDED, power > 0),
					11);

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F,
					world.rand.nextFloat() * 0.25F + 0.6F);

		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {

		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));

		if (state.getValue(EXTENDED)) {
			EnumFacing facing = state.getValue(FACING);
			AxisAlignedBB box = getSpikeHitbox(facing, pos);

			if (entityBox.intersects(box)) {
				if (facing == EnumFacing.UP) {
					if (entityBox.intersects(bottomBox(facing, pos)) && entityBox.intersects(topBox(facing, pos))) {
						collidingBoxes.add(box);
					}
				}
			}

		}
	}

	public AxisAlignedBB bottomBox(EnumFacing facing, BlockPos pos) {
		Logger.info(
				getSpikeHitbox(facing, pos).toString() + " --- " + contract(getSpikeHitbox(facing, pos), facing, 0.5));
		return contract(getSpikeHitbox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB topBox(EnumFacing facing, BlockPos pos) {
		return offset(bottomBox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB offset(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.offset(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB expand(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.expand(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB contract(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.contract(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public static AxisAlignedBB getSpikeHitbox(EnumFacing facing, BlockPos pos) {
		AxisAlignedBB result = new AxisAlignedBB(pos.offset(facing));
		double topPadding = 0.3;
		double sidePadding = 0.2;
		return result
				.contract(facing.getFrontOffsetX() == 1 ? topPadding : sidePadding * 2,
						facing.getFrontOffsetY() == 1 ? topPadding : sidePadding * 2,
						facing.getFrontOffsetZ() == 1 ? topPadding : sidePadding * 2)
				.offset(facing.getFrontOffsetX() == 1 ? 0 : sidePadding,
						facing.getFrontOffsetY() == 1 ? 0 : sidePadding,
						facing.getFrontOffsetZ() == 1 ? 0 : sidePadding);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(EXTENDED);
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return SkewerTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState blockState) {
		return new SkewerTileEntity();
	}

	public static class SkewerTileEntity extends TileEntity implements ITickable {

		@Override
		public void update() {

			EnumFacing facing = world.getBlockState(pos).getValue(FACING);
			for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class,
					getSpikeHitbox(facing, pos))) {
				Logger.info("attack entity");
				entity.attackEntityFrom(ModDamageSource.SKEWER, 1);
			}

			/*
			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null,
					getSpikeHitbox(world.getBlockState(pos)).offset(pos))) {
			
				Logger.info("attack entity");
				if (entity instanceof EntityLivingBase) {
					Logger.info("attack entity 2");
					entity.attackEntityFrom(ModDamageSource.SKEWER, 1);
				}
			}
			*/
		}
	}

}