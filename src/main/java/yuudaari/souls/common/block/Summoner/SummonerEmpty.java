package yuudaari.souls.common.block.Summoner;

import yuudaari.souls.common.util.Material;
import yuudaari.souls.common.ModObjects;
import yuudaari.souls.common.Config;
import yuudaari.souls.common.block.SoulsBlock;
import yuudaari.souls.common.item.Soulbook;
import yuudaari.souls.common.util.MobTarget;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SummonerEmpty extends SoulsBlock {
	public SummonerEmpty() {
		super("summoner_empty", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void registerRecipes() {
		//addRecipeShaped(new String[] { "BBB", "BEB", "BBB" }, 'B', ModObjects.get("endersteel_bars").getItem(), 'E',
		//		ModObjects.getItem("dust_ender").getItem());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem == null || heldItem.getItem() != ModObjects.getItem("soulbook"))
			return false;

		String mobTarget = MobTarget.getMobTarget(heldItem);
		int containedEssence = Soulbook.getContainedEssence(heldItem);
		if (mobTarget == null || containedEssence < Config.getSoulInfo(mobTarget).neededForSoul)
			return false;

		IBlockState mobSpawner = ((Summoner) ModObjects.get("summoner")).getDefaultState();
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