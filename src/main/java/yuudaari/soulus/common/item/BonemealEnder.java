package yuudaari.soulus.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.registration.BlockRegistry;

public class BonemealEnder extends Bonemeal {

	public BonemealEnder () {
		super("dust_ender");
		setHasGlint();
		addOreDict("dustEnder");
	}

	@Override
	public EnumRarity getRarity (ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		boolean flag = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
		BlockPos blockpos = flag ? pos : pos.offset(facing);
		ItemStack itemstack = player.getHeldItem(hand);

		if (player.canPlayerEdit(blockpos, facing, itemstack) && //
			world.mayPlace(world.getBlockState(blockpos).getBlock(), blockpos, false, facing, (Entity) null) && //
			BlockRegistry.DUST_ENDER.canPlaceBlockAt(world, blockpos)) {

			world.setBlockState(blockpos, BlockRegistry.DUST_ENDER.getDefaultState());

			itemstack.shrink(1);
			return EnumActionResult.SUCCESS;

		} else {
			return EnumActionResult.FAIL;
		}
	}
}
