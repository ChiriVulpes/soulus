package yuudaari.soulus.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.Tuple2;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.enderlink.EnderlinkTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigEnderlink;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;

@ConfigInjected(Soulus.MODID)
public class DustEnderBlock extends ModBlock {

	@SubscribeEvent
	public static final void rightClickBlock (PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().getItem() == Items.DYE && //
			event.getWorld().getBlockState(event.getPos()).getBlock() instanceof DustEnderBlock) {

			event.setUseBlock(Result.ALLOW);
		}
	}

	@Inject public static ConfigEnderlink CONFIG;

	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum
		.<EnumDyeColor>create("color", EnumDyeColor.class);

	public DustEnderBlock () {
		super("dust_ender_block", new Material(MapColor.GRASS).setTransparent().setDestroyOnPushed());
		setDefaultState(getDefaultState().withProperty(COLOR, EnumDyeColor.LIGHT_BLUE));
		addOreDict("dustEnder", "bonemeal");
		setSoundType(SoundType.STONE);
	}

	@Override
	public CreativeTab getCreativeTabToDisplayOn () {
		return null;
	}

	@Override
	public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		if (player.isSneaking()) return false;

		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() != Items.DYE) return false;

		EnumDyeColor newColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
		if (state.getValue(COLOR) == newColor) return false;

		world.setBlockState(pos, state.withProperty(COLOR, newColor), 3);
		stack.shrink(1);

		return true;
	}

	@Override
	public void onEntityCollidedWithBlock (World world, BlockPos pos, IBlockState state, Entity entity) {
		if (world.isRemote) return;
		if (world.rand.nextFloat() > CONFIG.teleportChance) return;

		if (!entity.getEntityBoundingBox().intersects(getBoundingBox(state, world, pos).offset(pos))) return;

		List<Tuple2<EnderlinkTileEntity, Double>> links = new ArrayList<>();

		EnumDyeColor color = world.getBlockState(pos).getValue(COLOR);
		for (int x = -2; x < 3; x++) {
			for (int z = -2; z < 3; z++) {
				int cx = (pos.getX() >> 4) + x, cz = (pos.getZ() >> 4) + z;
				for (TileEntity te : world.getChunkFromChunkCoords(cx, cz).getTileEntityMap().values()) {

					if (!(te instanceof EnderlinkTileEntity)) continue;

					EnderlinkTileEntity ete = (EnderlinkTileEntity) te;
					if (ete.color != color) continue;

					double distance = entity.getDistanceSqToCenter(ete.getPos());
					if (!ete.isWithinRange(entity, distance)) continue;

					links.add(new Tuple2<>(ete, distance));
				}
			}
		}

		links.stream()
			.sorted( (a, b) -> Double.compare(a._2, b._2))
			.findFirst()
			.ifPresent(ete -> ete._1.teleportEntity(entity));
	}

	@Override
	public boolean canHarvestBlock (IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox (IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0, 0, 0, 1, .125, 1);
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged (IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockAt(world, pos)) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	public boolean canPlaceBlockAt (World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public Item getItemDropped (IBlockState state, Random rand, int fortune) {
		return ModItems.BONEMEAL_ENDER;
	}

	@Override
	public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return ModItems.BONEMEAL_ENDER.getItemStack();
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			COLOR
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(COLOR).getMetadata();
	}

	/////////////////////////////////////////
	// Waila
	//

	@SideOnly(Side.CLIENT)
	@Override
	public final List<String> getWailaTooltip (List<String> currentTooltip, IDataAccessor accessor) {
		String color = accessor.getBlockState().getValue(COLOR).getDyeColorName();

		currentTooltip.add(I18n.format("waila.soulus:misc.color." + color));

		return currentTooltip;
	}
}
