package yuudaari.soulus.common.block.enderlink;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.DustEnderBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigEnderlink;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Material;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ConfigInjected(Soulus.MODID)
public class Enderlink extends UpgradeableBlock<EnderlinkTileEntity> {

	private static final Map<Long, Set<BlockPos>> ENDERLINKS = new HashMap<>();

	private static long getChunkPosition (BlockPos pos) {
		return getChunkPosition(pos.getX() >> 4, pos.getZ() >> 4);
	}

	private static long getChunkPosition (int cx, int cz) {
		return cx * (long) Integer.MAX_VALUE + cz;
	}

	public static Set<BlockPos> getEnderlinksInChunk (BlockPos pos) {
		return getEnderlinksInChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public static Set<BlockPos> getEnderlinksInChunk (int cx, int cz) {
		final long cp = getChunkPosition(cx, cz);
		final Set<BlockPos> enderlinks = ENDERLINKS.get(cp);
		return enderlinks == null ? new HashSet<>() : enderlinks;
	}

	public static void notifyEnderlink (EnderlinkTileEntity te) {
		long cp = getChunkPosition(te.getPos());

		Set<BlockPos> enderlinks = ENDERLINKS.get(cp);
		if (enderlinks == null) ENDERLINKS.put(cp, enderlinks = new HashSet<>());

		enderlinks.add(te.getPos());
	}

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		RANGE (2, "range", ModItems.ORB_MURKY.getItemStack());

		private final int index;
		private final String name;
		private final ItemStack stack;
		// by default all upgrades are capped at 16
		private int maxQuantity = 16;

		private Upgrade (int index, String name, ItemStack item) {
			this.index = index;
			this.name = name;
			this.stack = item;
		}

		@Override
		public int getIndex () {
			return index;
		}

		@Override
		public String getName () {
			return name;
		}

		@Override
		public int getMaxQuantity () {
			// all upgrades by default are capped at 16
			return maxQuantity;
		}

		@Override
		public void setMaxQuantity (int quantity) {
			maxQuantity = quantity;
		}

		@Override
		public boolean isItemStack (ItemStack stack) {
			if (stack.getItem() != this.stack.getItem())
				return false;

			if (name == "range") {
				return OrbMurky.isFilled(stack);
			}

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = new ItemStack(this.stack.getItem(), quantity);
			if (name == "range") {
				OrbMurky.setFilled(stack);
			}

			return stack;
		}
	}

	@Override
	public IUpgrade[] getUpgrades () {
		return Upgrade.values();
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigEnderlink CONFIG;

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public Enderlink () {
		super("enderlink", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP));
		setHasDescription();
		registerWailaProvider(DustEnderBlock.class);
	}

	@Override
	public UpgradeableBlock<EnderlinkTileEntity> getInstance () {
		return ModBlocks.ENDERLINK;
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			FACING
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity (IBlockState blockState) {
		return true;
	}

	@Override
	public Class<? extends UpgradeableBlockTileEntity> getTileEntityClass () {
		return EnderlinkTileEntity.class;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		notifyEnderlink((EnderlinkTileEntity) world.getTileEntity(pos));
	}

	@Override
	public void onBlockDestroy (World world, BlockPos pos, int fortune, EntityPlayer player) {
		long cp = getChunkPosition(pos);

		Set<BlockPos> enderlinks = ENDERLINKS.get(cp);
		if (enderlinks != null) {
			enderlinks.remove(pos);

			if (enderlinks.size() == 0) {
				ENDERLINKS.remove(cp);
			}
		}

		super.onBlockDestroy(world, pos, fortune, player);
	}

	@Override
	public boolean onActivateInsert (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		Item item = stack.getItem();
		EnderlinkTileEntity te = (EnderlinkTileEntity) world.getTileEntity(pos);

		// changing the colour/alignment of the enderlink
		if (item.equals(Items.DYE) && te.setColor(EnumDyeColor.byDyeDamage(stack.getItemDamage()))) {
			stack.shrink(1);
			return true;
		}

		// trying to insert the upgrades
		return super.onActivateInsert(world, pos, player, stack);
	}

	/////////////////////////////////////////
	// Waila
	//

	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, EnderlinkTileEntity te, EntityPlayer player) {
		if (te == null) return;

		currentTooltip.add(Translation.localize("waila.soulus:misc.color." + te.color.getDyeColorName()));
	}

}
