package yuudaari.soulus.common.registration.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class DispenserBehavior extends BehaviorDefaultDispenseItem {

	public static interface IDispense {

		public boolean onDispense (final IBlockSource source, final ItemStack stack, final BlockPos targetPos);
	}

	private final IDispense dispense;

	public DispenserBehavior (final IDispense dispense) {
		this.dispense = dispense;
	}

	@Override
	protected ItemStack dispenseStack (final IBlockSource source, final ItemStack stack) {
		final EnumFacing facing = (EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING);
		final IPosition position = BlockDispenser.getDispensePosition(source);

		final BlockPos targetPos = source.getBlockPos().offset(facing, 1);

		if (dispense.onDispense(source, stack, targetPos))
			doDispense(source.getWorld(), stack.splitStack(1), 6, facing, position);

		return stack;
	}
}
