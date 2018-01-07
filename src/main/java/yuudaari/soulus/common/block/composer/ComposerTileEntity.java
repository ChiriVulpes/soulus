package yuudaari.soulus.common.block.composer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;

public class ComposerTileEntity extends HasRenderItemTileEntity {

	public boolean isConnected = false;
	private float timeTillCraft = 5;
	private float lastTimeTillCraft = 10;

	private ItemStack itemCrafting;
	private double itemRotation = 0;
	private double prevItemRotation = 0;

	@Override
	public double getItemRotation() {
		return itemRotation;
	}

	@Override
	public double getPrevItemRotation() {
		return prevItemRotation;
	}

	@Override
	public ItemStack getStoredItem() {
		return itemCrafting;
	}

	@Override
	public boolean shouldComplexRotate() {
		return true;
	}

	@Override
	public Composer getBlock() {
		return Composer.INSTANCE;
	}

	private double activationAmount() {
		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) {
			return 0;
		}

		double activationAmount = 0;

		for (EntityPlayer player : world.playerEntities) {

			if (EntitySelectors.NOT_SPECTATING.apply(player)) {
				double d0 = player.getDistanceSqToCenter(pos);

				double nearAmt = (d0 / (20));
				activationAmount += Math.max(0, (1 - (nearAmt * nearAmt)) * 2);
			}
		}

		return activationAmount;
	}

	@Override
	public void update() {
		validateStructure();

		double activationAmount = activationAmount();
		if (activationAmount <= 0) {
			// ease rotation to a stop
			double diff = itemRotation - prevItemRotation;
			prevItemRotation = itemRotation;
			itemRotation = itemRotation + diff * 0.9;
			return;
		}

		updateRenderer();
	}

	public void validateStructure() {
		EnumFacing direction = getBlock().validateStructure(world, pos);
		isConnected = direction != null;

		IBlockState state = world.getBlockState(pos);
		boolean changedState = false;

		if (isConnected && state.getValue(Composer.FACING) != direction) {
			state = state.withProperty(Composer.FACING, direction);
			changedState = true;
		}

		if (state.getValue(Composer.CONNECTED) != isConnected) {
			state = state.withProperty(Composer.CONNECTED, isConnected);
			changedState = true;

			BlockPos center = direction == null ? null : pos.offset(direction, -3);

			getBlock().structure.loopBlocks(world, pos, state.getValue(Composer.FACING),
					(BlockPos pos2, BlockValidator validator) -> {
						IBlockState currentState = world.getBlockState(pos2);

						if (currentState.getBlock() == ComposerCell.INSTANCE) {
							world.setBlockState(pos2,
									currentState.withProperty(ComposerCell.CELL_STATE,
											!isConnected ? ComposerCell.CellState.DISCONNECTED
													: pos2.equals(center) ? ComposerCell.CellState.CONNECTED_CENTER
															: ComposerCell.CellState.CONNECTED_EDGE),
									3);

							ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(pos2);
							ccte.composerLocation = isConnected ? pos : null;
							ccte.blockUpdate();
						}

						return null;
					});
		}

		if (changedState)
			world.setBlockState(pos, state, 3);
	}

	public void updateRenderer() {
		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation = itemRotation + 1.0F * getCompositionPercent() + diff * 0.8;
	}

	public float getCompositionPercent() {
		return (lastTimeTillCraft - timeTillCraft) / (float) lastTimeTillCraft;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}