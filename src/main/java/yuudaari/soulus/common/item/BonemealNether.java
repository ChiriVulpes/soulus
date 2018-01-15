package yuudaari.soulus.common.item;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleManager;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigBonemealNether;

@ConfigInjected(Soulus.MODID)
public class BonemealNether extends Bonemeal {

	@Inject(ConfigBonemealNether.class) public static ConfigBonemealNether CONFIG;

	public BonemealNether () {
		super("bone_meal_nether");
	}

	@Override
	public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof IGrowable && !state.isFullBlock() && !(block instanceof BlockDoublePlant)) {
			world.setBlockState(pos, ModBlocks.ASH.getDefaultState(), 3);

			ItemStack stack = player.getHeldItem(hand);
			stack.shrink(1);

			if (world.isRemote) {
				particles(world, pos);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	private static void particles (World world, BlockPos pos) {
		Random rand = world.rand;

		for (int i = 0; i < CONFIG.particleCount; ++i) {
			double d3 = (pos.getX() - 0.5F + rand.nextFloat());
			double d4 = (pos.getY() + rand.nextFloat());
			double d5 = (pos.getZ() - 0.5F + rand.nextFloat());
			double d3o = (d3 - pos.getX()) / 4;
			double d4o = (d4 - pos.getY()) / 5;
			double d5o = (d5 - pos.getZ()) / 4;
			ParticleManager.spawnParticle(world, ParticleType.BLACKENED_BONEMEAL
				.getId(), false, d3 + 0.5F, d4, d5 + 0.5F, d3o, d4o, d5o, 1);
		}
	}
}
