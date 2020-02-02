package yuudaari.soulus.common.item;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigBonemealNether;
import yuudaari.soulus.common.registration.BlockRegistry;

@ConfigInjected(Soulus.MODID)
public class BonemealNether extends Bonemeal {

	@Inject public static ConfigBonemealNether CONFIG;

	public BonemealNether () {
		super("bone_meal_nether");
	}

	@Override
	public EnumRarity getRarity (ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return bonemealBlock(world, pos, player.getHeldItem(hand)) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public boolean onDispense (final IBlockSource source, final ItemStack stack, final BlockPos targetPos) {
		return bonemealBlock(source.getWorld(), targetPos, stack);
	}

	private static boolean bonemealBlock (final World world, final BlockPos pos, final ItemStack stack) {
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();

		if (state.isFullBlock())
			return true;

		if (!(block instanceof IGrowable) && !(block instanceof BlockBush) && !(block instanceof BlockReed))
			return true;

		if (block instanceof BlockDoublePlant)
			world.setBlockState(state.getValue(BlockDoublePlant.HALF) == EnumBlockHalf.UPPER ? pos.down() : pos.up(), Blocks.AIR.getDefaultState());

		IBlockState newState = BlockRegistry.ASH.getDefaultState();
		world.setBlockState(pos, newState, 3);
		BlockRegistry.ASH.neighborChanged(state, world, pos, BlockRegistry.ASH, pos);

		stack.shrink(1);

		if (world.isRemote)
			particles(world, pos);

		return false;
	}

	@SideOnly(Side.CLIENT)
	private static void particles (World world, BlockPos pos) {
		Random rand = world.rand;

		for (int i = 0; i < CONFIG.particleCount; ++i) {
			double x = (pos.getX() - 0.5F + rand.nextFloat());
			double y = (pos.getY() + rand.nextFloat());
			double z = (pos.getZ() - 0.5F + rand.nextFloat());
			double xv = (x - pos.getX()) / 4;
			double yv = (y - pos.getY()) / 5;
			double zv = (z - pos.getZ()) / 4;
			world.spawnParticle(ParticleType.BONEMEAL_NETHER.getId(), false, x + 0.5F, y, z + 0.5F, xv, yv, zv, 1);
		}
	}

}
