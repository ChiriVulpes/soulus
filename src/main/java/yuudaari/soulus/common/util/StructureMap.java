package yuudaari.soulus.common.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureMap {

	public final Map<BlockPos, BlockValidator> blocks = new HashMap<>();

	public StructureMap addBlock (int x, int y, int z, BlockValidator validator) {
		blocks.put(new BlockPos(x, y, z), validator);
		return this;
	}

	public StructureMap addRowX (int x, int y, int z, int length, BlockValidator validator) {
		for (int i = 0; i < length; i++) {
			addBlock(x + i, y, z, validator);
		}

		return this;
	}

	public StructureMap addRowZ (int x, int y, int z, int length, BlockValidator validator) {
		for (int i = 0; i < length; i++) {
			addBlock(x, y, z + i, validator);
		}

		return this;
	}

	public StructureMap addColumn (int x, int y, int z, int height, BlockValidator validator) {
		for (int i = 0; i < height; i++) {
			addBlock(x, y + i, z, validator);
		}

		return this;
	}

	/**
	 * @param facing Do not call with EnumFacing.DOWN or EnumFacing.UP
	 */
	public boolean isValid (World world, BlockPos pos, EnumFacing facing) {
		return loopBlocks(world, pos, facing, (BlockPos pos2, BlockValidator validator) -> {
			if (!validator.validate(pos, world, pos2, world.getBlockState(pos2)))
				return false;
			return null;
		}, true);
	}

	/**
	 * @param facing Do not call with EnumFacing.DOWN or EnumFacing.UP
	 */
	public Boolean loopBlocks (World world, BlockPos pos, EnumFacing facing, BlockLoop loop) {
		return loopBlocks(world, pos, facing, loop, null);
	}

	/**
	 * @param facing Do not call with EnumFacing.DOWN or EnumFacing.UP
	 */
	public Boolean loopBlocks (World world, BlockPos pos, EnumFacing facing, BlockLoop loop, Boolean defaultResult) {
		EnumFacing z = facing;
		EnumFacing x = facing.rotateY();
		EnumFacing y = EnumFacing.UP;

		for (Map.Entry<BlockPos, BlockValidator> entry : blocks.entrySet()) {
			BlockPos mapPos = entry.getKey();
			BlockPos offsetPosition = pos.offset(x, mapPos.getX()).offset(y, mapPos.getY()).offset(z, mapPos.getZ());
			Boolean result = loop.handle(offsetPosition, entry.getValue());
			// Logger.info("pos: " + pos + ", mapPos: " + mapPos + ", calculated: " + offsetPosition + ", result: " + result);
			if (result != null)
				return result;
		}

		return defaultResult;
	}

	public static interface BlockLoop {

		public Boolean handle (BlockPos pos, BlockValidator validator);
	}

	public static interface BlockValidator {

		public boolean validate (BlockPos pos, World world, BlockPos checkPos, IBlockState state);

		public static BlockValidator byBlockState (IBlockState... validStates) {
			return (pos, world, checkPos, state) -> {
				for (IBlockState validState : validStates)
					if (state.equals(validState))
						return true;
				return false;
			};
		}

		public static BlockValidator byBlock (Block... validBlocks) {
			return (pos, world, checkPos, state) -> {
				for (Block validBlock : validBlocks)
					if (state.getBlock().equals(validBlock))
						return true;
				return false;
			};
		}
	}
}
