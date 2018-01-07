package yuudaari.soulus.common.block.composer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;

public class ComposerTileEntity extends HasRenderItemTileEntity {

	private boolean isConnected = false;
	private ItemStack itemCrafting;
	private float timeTillCraft = 5;
	private float lastTimeTillCraft = 10;

	@Override
	public Composer getBlock() {
		return Composer.INSTANCE;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
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

		updateRenderer(activationAmount);
	}

	private Map<BlockPos, Byte> cellMap = new HashMap<>();

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
			BlockPos x = direction == null ? null : BlockPos.ORIGIN.offset(direction, 1);
			BlockPos z = direction == null ? null : BlockPos.ORIGIN.offset(direction.rotateY(), 1);
			BlockPos topLeft = direction == null ? null : offset(offset(pos, x, -4), z, -1);

			if (isConnected) {
				cellMap.clear();
				for (int iz = 0; iz < 3; iz++) {
					for (int ix = 0; ix < 3; ix++) {
						BlockPos cellPos = offset(offset(topLeft, x, ix), z, iz);
						cellMap.put(cellPos, (byte) (ix + iz * 3));
					}
				}
			}

			loopComposerCells(ccte -> {
				BlockPos ccPos = ccte.getPos();
				IBlockState currentState = world.getBlockState(ccPos);
				world.setBlockState(ccPos,
						currentState.withProperty(ComposerCell.CELL_STATE,
								!isConnected ? ComposerCell.CellState.DISCONNECTED
										: ccPos.equals(center) ? ComposerCell.CellState.CONNECTED_CENTER
												: ComposerCell.CellState.CONNECTED_EDGE),
						3);

				ccte.composerLocation = isConnected ? pos : null;
				ccte.changeComposerCooldown = 20;
				Byte slot = cellMap.get(ccPos);
				ccte.slot = slot == null ? -1 : slot;
				ccte.blockUpdate();

				return null;
			});
		}

		if (changedState)
			world.setBlockState(pos, state, 3);
	}

	private BlockPos offset(BlockPos a, BlockPos b, double amt) {
		return a.add(b.getX() * amt, b.getY() * amt, b.getZ() * amt);
	}

	public void updateRenderer(double activationAmount) {
		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation += activationAmount <= 0 ? //
				diff * 0.9 // ease rotation to a stop
				: 1.0F * getCompositionPercent() + diff * 0.8; // normal rotation
	}

	public float getCompositionPercent() {
		return (lastTimeTillCraft - timeTillCraft) / (float) lastTimeTillCraft;
	}

	public void loopComposerCells(ComposerCellHandler handler) {
		IBlockState state = world.getBlockState(pos);

		getBlock().structure.loopBlocks(world, pos, state.getValue(Composer.FACING),
				(BlockPos pos2, BlockValidator validator) -> {
					IBlockState currentState = world.getBlockState(pos2);

					if (currentState.getBlock() == ComposerCell.INSTANCE) {
						ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(pos2);
						return handler.handle(ccte);
					}

					return null;
				});
	}

	public static interface ComposerCellHandler {
		public Boolean handle(ComposerCellTileEntity te);
	}

	/////////////////////////////////////////
	// Renderer
	//

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
}