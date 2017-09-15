package yuudaari.souls.common.block.Summoner;

import yuudaari.souls.Souls;
import yuudaari.souls.common.Config;
import yuudaari.souls.common.ModObjects;
import yuudaari.souls.common.block.SoulsBlock;
import yuudaari.souls.common.item.Soulbook;
import yuudaari.souls.common.util.Material;
import yuudaari.souls.common.util.MobTarget;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Souls.MODID)
public class Summoner extends SoulsBlock {

	private static String getSpawnerMobTarget(SummonerTileEntity summonerTileEntity) {
		return summonerTileEntity.getMob();
	}

	public Summoner() {
		super("summoner", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
	}

	@SubscribeEvent
	public void registerTileEntities(RegistryEvent.Register<Item> event) {
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return SummonerTileEntity.class;
	}

	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public TileEntity createTileEntity(World worldIn, IBlockState blockState) {
		return new SummonerTileEntity();
	}

	public static String lastBrokenSummonerMobTarget = null;

	@SubscribeEvent
	public static void onSummonerBreak(BlockEvent.BreakEvent event) {
		if (event.getState().getBlock() == ModObjects.getBlock("summoner")) {
			SummonerTileEntity tileEntity = (SummonerTileEntity) event.getWorld().getTileEntity(event.getPos());
			if (tileEntity == null)
				throw new RuntimeException("Summoner has no tile entity");
			lastBrokenSummonerMobTarget = tileEntity.getMob();
		}
	}

	@Nonnull
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<>();

		drops.add(ModObjects.getBlock("summoner_empty").getItemStack());

		ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
		MobTarget.setMobTarget(soulbook, lastBrokenSummonerMobTarget);
		Soulbook.setContainedEssence(soulbook, Config.getSoulInfo(lastBrokenSummonerMobTarget).neededForSoul);
		drops.add(soulbook);

		return drops;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			SummonerTileEntity mobSpawner = (SummonerTileEntity) world.getTileEntity(pos);
			String mobTarget = Summoner.getSpawnerMobTarget(mobSpawner);
			ItemStack soulbook = ModObjects.getItem("soulbook").getItemStack();
			MobTarget.setMobTarget(soulbook, mobTarget);
			Soulbook.setContainedEssence(soulbook, Config.getSoulInfo(mobTarget).neededForSoul);
			EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, soulbook);
			dropItem.setNoPickupDelay();
			world.spawnEntity(dropItem);
		}
		world.setBlockState(pos, ModObjects.getBlock("summonerEmpty").getDefaultState());
		return true;
	}
}
