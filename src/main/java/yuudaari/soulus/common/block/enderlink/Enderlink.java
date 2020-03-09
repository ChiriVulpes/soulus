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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.block.DustEnderBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigEnderlink;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.ItemStackMutable;
import yuudaari.soulus.common.util.Material;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

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

		RANGE (2, "range", ItemRegistry.ORB_MURKY);

		private final int index;
		private final String name;
		private final Item item;
		// by default all upgrades are capped at 16
		private int maxQuantity = 16;

		private Upgrade (int index, String name, Item item) {
			this.index = index;
			this.name = name;
			this.item = item;
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
		public Item getItem () {
			return item;
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
			if (!IUpgrade.super.isItemStack(stack))
				return false;

			if (this == Upgrade.RANGE)
				return OrbMurky.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			final ItemStack stack = IUpgrade.super.getItemStack(quantity);

			if (this == Upgrade.RANGE)
				OrbMurky.setFilled(stack);

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
		setTool(Tool.PICK, 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP));
		setHasDescription();
		registerWailaProvider(DustEnderBlock.class);
	}

	@Override
	public UpgradeableBlock<EnderlinkTileEntity> getInstance () {
		return BlockRegistry.ENDERLINK;
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.RARE;
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
	public List<ItemStack> onBlockDestroy (final World world, final BlockPos pos, final int fortune, final EntityPlayer player) {
		final long cp = getChunkPosition(pos);

		final Set<BlockPos> enderlinks = ENDERLINKS.get(cp);
		if (enderlinks != null) {
			enderlinks.remove(pos);

			if (enderlinks.size() == 0)
				ENDERLINKS.remove(cp);
		}

		return super.onBlockDestroy(world, pos, fortune, player);
	}

	@Override
	public Stream<Item> getAcceptedItems () {
		return Stream.of( //
			super.getAcceptedItems(), //
			Stream.of(Items.DYE) //
		)
			.flatMap(Function.identity());
	}

	@Override
	public boolean acceptsItemStack (ItemStack stack, World world, BlockPos pos) {
		return stack.getItem().equals(Items.DYE) || super.acceptsItemStack(stack, world, pos);
	}

	@Override
	public boolean onActivateInsert (final World world, final BlockPos pos, final @Nullable EntityPlayer player, final ItemStackMutable stack) {
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
