package yuudaari.soulus.common.block.soul_totem;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.soul_totem.SoulTotem.Upgrade;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;

public class SoulTotemTileEntity extends UpgradeableBlockTileEntity {

	@Override
	public SoulTotem getBlock () {
		return ModBlocks.SOUL_TOTEM;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean isActive () {
		return isConnected && upgrades.get(Upgrade.SOUL_CATALYST) == 1;
	}

	private boolean isConnected = false;

	@Override
	public void update () {
		validateStructure();


	}

	private void validateStructure () {
		IBlockState state = world.getBlockState(pos);
		boolean structureValid = getBlock().structure.isValid(world, pos);

		if (state.getValue(SoulTotem.CONNECTED) != structureValid) {
			world.setBlockState(pos, state.withProperty(SoulTotem.CONNECTED, structureValid), 3);
		}

		isConnected = structureValid;
	}

	public int getSignalStrength () {
		return isActive() ? 15 : 0;
	}
}
