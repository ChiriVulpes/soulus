package yuudaari.soulus.common.block.summoner;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.util.MobTarget;
import yuudaari.soulus.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SummonerEmpty extends ModBlock {
	public SummonerEmpty() {
		super("summoner_empty", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem == null || heldItem.getItem() != ModItems.SOULBOOK)
			return false;

		String mobTarget = MobTarget.getMobTarget(heldItem);
		int containedEssence = Soulbook.getContainedEssence(heldItem);
		if (mobTarget == null || containedEssence < Soulus.getSoulInfo(mobTarget).quantity)
			return false;

		IBlockState mobSpawner = ((Summoner) ModBlocks.SUMMONER).getDefaultState();
		world.setBlockState(pos, mobSpawner);

		SummonerTileEntity tileEntity = (SummonerTileEntity) world.getTileEntity(pos);
		if (tileEntity == null) {
			throw new RuntimeException("Mob spawner tile entity was not created. Something went wrong.");
		}

		tileEntity.setMob(mobTarget);
		player.inventory.removeStackFromSlot(player.inventory.currentItem);

		return true;
	}
}