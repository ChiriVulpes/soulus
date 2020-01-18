package yuudaari.soulus.common.misc;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.ItemStackMutable;

public class DispenserBehaviorUpgrade extends BehaviorDefaultDispenseItem {

	public static interface IInsertsItemStacks {

		Stream<Item> getAcceptedItems ();

		boolean acceptsItemStack (final ItemStack stack, final World world, final BlockPos pos);

		boolean onActivateInsert (final World world, final BlockPos pos, final @Nullable EntityPlayer player, final ItemStackMutable output);
	}

	public static void register () {
		final DispenserBehaviorUpgrade behavior = new DispenserBehaviorUpgrade();
		BlockRegistry.getUpgradeableBlocks()
			.flatMap(block -> block.getAcceptedItems())
			.forEach(item -> BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, behavior));
	}

	@Override
	protected ItemStack dispenseStack (final IBlockSource source, final ItemStack stack) {
		final EnumFacing facing = (EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING);
		final IPosition position = BlockDispenser.getDispensePosition(source);

		boolean dispense = true;

		final BlockPos targetPos = source.getBlockPos().offset(facing, 1);
		final World world = source.getWorld();
		final IBlockState targetState = world.getBlockState(targetPos);
		final Block targetBlock = targetState.getBlock();

		final ItemStackMutable mutableStack = new ItemStackMutable(stack);

		if (targetBlock instanceof IInsertsItemStacks) {
			final IInsertsItemStacks block = (IInsertsItemStacks) targetBlock;
			if (block.acceptsItemStack(stack, world, targetPos)) {
				dispense = false;
				block.onActivateInsert(world, targetPos, null, mutableStack);
			}
			// final UpgradeableBlockTileEntity tileEntity = (UpgradeableBlockTileEntity) world.getTileEntity(targetPos);
			// if (tileEntity.insertUpgrade(stack, upgrade, quantity)) {

			// }
		}

		if (dispense)
			doDispense(source.getWorld(), mutableStack.splitStack(1), 6, facing, position);

		return mutableStack.getImmutable();
	}
}
