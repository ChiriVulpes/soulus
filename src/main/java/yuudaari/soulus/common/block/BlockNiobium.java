package yuudaari.soulus.common.block;

import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlock;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlockTileEntity;
import yuudaari.soulus.common.block.soul_totem.SoulTotem;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.Material;

public class BlockNiobium extends ComparatorInteractiveBlock {

	public BlockNiobium () {
		super("block_niobium", new Material(MapColor.BLUE));
		setHardness(5F);
		setResistance(30F);
		setTool(Tool.PICK, 1);
		setSoundType(SoundType.METAL);
		addOreDict("blockSoulusNiobium");
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void addCollisionBoxToList (IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));

		/*
		 * The following code is to assure that when connected to a soul totem, the soul totem has collision detection
		 * on the corners
		 */
		IBlockState connectedTotem = BlockRegistry.SOUL_TOTEM.getDefaultState().withProperty(SoulTotem.CONNECTED, true);

		BlockPos totemPos = pos.add(-1, 1, -1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			BlockRegistry.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(1, 1, -1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			BlockRegistry.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(-1, 1, 1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			BlockRegistry.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);

		totemPos = pos.add(1, 1, 1);
		if (world.getBlockState(totemPos).equals(connectedTotem))
			BlockRegistry.SOUL_TOTEM
				.addCollisionBoxToList(connectedTotem, world, totemPos, entityBox, collidingBoxes, entityIn, isActualState);
	}

	@Override
	public Class<? extends ComparatorInteractiveBlockTileEntity> getTileEntityClass () {
		return BlockNiobiumTileEntity.class;
	}

	@Override
	public ComparatorInteractiveBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new BlockNiobiumTileEntity();
	}

	public static class BlockNiobiumTileEntity extends ComparatorInteractiveBlockTileEntity {

		@Override
		protected int getSignal (int signalIn) {
			return 15 - signalIn;
		}
	}
}
