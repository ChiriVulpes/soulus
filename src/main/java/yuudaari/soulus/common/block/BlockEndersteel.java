package yuudaari.soulus.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlock;
import yuudaari.soulus.common.block.comparator_interactive_block.ComparatorInteractiveBlockTileEntity;
import yuudaari.soulus.common.util.Material;

public class BlockEndersteel extends ComparatorInteractiveBlock {

	public BlockEndersteel () {
		super("block_endersteel", new Material(MapColor.GRASS));
		setHardness(5F);
		setResistance(30F);
		setTool(Tool.PICK, 1);
		setSoundType(SoundType.METAL);
		addOreDict("blockSoulusEndersteel");
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public Class<? extends ComparatorInteractiveBlockTileEntity> getTileEntityClass () {
		return BlockEndersteelTileEntity.class;
	}

	@Override
	public ComparatorInteractiveBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new BlockEndersteelTileEntity();
	}

	public static class BlockEndersteelTileEntity extends ComparatorInteractiveBlockTileEntity {

		@Override
		protected int getSignal (int signalIn) {
			return signalIn == 0 ? 0 : (int) Math.floor(Math
				.sin((double) world.getTotalWorldTime() / (5 * (1 + Math.pow(15 - signalIn, 3)))) * 7.5 + 8.5);
		}
	}
}
