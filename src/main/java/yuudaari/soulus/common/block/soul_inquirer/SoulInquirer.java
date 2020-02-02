package yuudaari.soulus.common.block.soul_inquirer;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.registration.Registration.ItemBlock;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ItemStackMutable;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class SoulInquirer extends UpgradeableBlock<SoulInquirerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {

		RANGE (0, "range", ItemRegistry.ORB_MURKY),
		COUNT (1, "count", ItemRegistry.CRYSTAL_BLOOD);

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

			if (this == Upgrade.COUNT)
				return CrystalBlood.isFilled(stack);

			if (this == Upgrade.RANGE)
				return OrbMurky.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = IUpgrade.super.getItemStack(quantity);

			if (this == Upgrade.COUNT)
				CrystalBlood.setFilled(stack);

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

	@Inject public static ConfigEssences CONFIG_ESSENCES;

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyBool HAS_SOULBOOK = PropertyBool.create("has_soulbook");

	public SoulInquirer () {
		super("soul_inquirer", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.STONE);
		setHardness(3F);
		disableStats();
		setDefaultState(getDefaultState().withProperty(HAS_SOULBOOK, false));
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<SoulInquirerTileEntity> getInstance () {
		return BlockRegistry.SOUL_INQUIRER;
	}

	@Override
	public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}

	@Override
	public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 0, 1, .5625, 1));
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return state.getValue(HAS_SOULBOOK);
	}

	@Override
	public int getComparatorInputOverride (IBlockState state, World world, BlockPos pos) {
		SoulInquirerTileEntity te = (SoulInquirerTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.getSignalStrength();
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			HAS_SOULBOOK
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(HAS_SOULBOOK, meta == 0 ? false : true);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(HAS_SOULBOOK) ? 0 : 1;
	}

	/////////////////////////////////////////
	// Item
	//

	@Override
	public ItemBlock createItemBlock () {
		return new Registration.ItemBlock(this) {

			@Override
			public String getItemStackDisplayName (ItemStack stack) {
				String essenceType = EssenceType.getEssenceType(stack);
				ConfigEssence config = CONFIG_ESSENCES.get(essenceType);
				if (essenceType == null || config == null)
					return Translation.localize(this.getUnlocalizedName() + ".unfocused.name").trim();

				String alignment = config.name;
				if (alignment == null)
					alignment = EssenceType.localize(essenceType);

				return Translation.localize(this.getUnlocalizedName() + ".focused.name", alignment).trim();
			}
		};
	}

	public ItemStack getItemStack (SoulInquirerTileEntity te, int count, int metadata) {
		ItemStack itemStack = new ItemStack(getItemBlock(), count, metadata);

		if (te != null) {
			itemStack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
		}

		return itemStack;
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity (IBlockState blockState) {
		return blockState.getValue(HAS_SOULBOOK);
	}

	@Override
	public Class<? extends UpgradeableBlockTileEntity> getTileEntityClass () {
		return SoulInquirerTileEntity.class;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public boolean onActivateEmptyHand (World world, BlockPos pos, EntityPlayer player) {
		IBlockState state = world.getBlockState(pos);
		if (!state.getValue(HAS_SOULBOOK))
			return false;

		boolean returnedUpgrade = super.onActivateEmptyHand(world, pos, player);

		if (!returnedUpgrade) {
			SoulInquirerTileEntity te = (SoulInquirerTileEntity) world.getTileEntity(pos);

			returnItemsToPlayer(world, Collections.singletonList(Soulbook.getFilled(te.getEssenceType())), player);

			world.setBlockState(pos, getDefaultState().withProperty(HAS_SOULBOOK, false));

			return true;
		}

		return false;
	}

	@Override
	public void addOtherDropStacksToList (List<ItemStack> list, World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te != null && te instanceof SoulInquirerTileEntity) {
			SoulInquirerTileEntity ste = (SoulInquirerTileEntity) te;
			list.add(Soulbook.getFilled(ste.getEssenceType()));
		}
	}

	@Override
	public boolean onActivateEmptyHandSneaking (World world, BlockPos pos, EntityPlayer player) {
		super.onActivateEmptyHandSneaking(world, pos, player);

		world.setBlockState(pos, getDefaultState().withProperty(HAS_SOULBOOK, false));

		return true;
	}

	@Override
	public Stream<Item> getAcceptedItems () {
		return Stream.of( //
			super.getAcceptedItems(),  //
			Stream.of(ItemRegistry.SOULBOOK) //
		)
			.flatMap(Function.identity());
	}

	@Override
	public boolean acceptsItemStack (final ItemStack stack, final World world, final BlockPos pos) {
		return super.acceptsItemStack(stack, world, pos) || Soulbook.isFilled(stack);
	}

	@Override
	public boolean onActivateInsert (final World world, final BlockPos pos, final @Nullable EntityPlayer player, final ItemStackMutable stack) {
		Item item = stack.getItem();
		IBlockState state = world.getBlockState(pos);


		// try to insert a soulbook
		if (item == ItemRegistry.SOULBOOK) {
			if (!Soulbook.isFilled(stack.getImmutable()))
				return false;

			if (!state.getValue(HAS_SOULBOOK)) {
				world.setBlockState(pos, getDefaultState().withProperty(HAS_SOULBOOK, true));
			}

			SoulInquirerTileEntity te = (SoulInquirerTileEntity) world.getTileEntity(pos);

			// there was already a tile entity here, with an essence type
			// that means there's a soulbook inside, so return it
			String oldEssenceType = te.getEssenceType();
			if (oldEssenceType != null) {
				returnItemsToPlayer(world, Collections.singletonList(Soulbook.getFilled(te.getEssenceType())), player);
			}

			String newEssenceType = EssenceType.getEssenceType(stack.getImmutable());
			te.setEssenceType(newEssenceType);

			te.reset();

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
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, SoulInquirerTileEntity te, EntityPlayer player) {
		if (te == null) return;

		final int upgradeCount = te.upgrades.get(Upgrade.COUNT);

		if (upgradeCount == 0) {
			final String detection = te.soulInquiry() > 0 ? "something" : "nothing";
			currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":soul_inquirer.detecting_" + detection));
		} else {
			currentTooltip.add(new Translation("waila." + Soulus.MODID + ":soul_inquirer.detecting")
				.addArgs(Math.min(15, te.soulInquiry()), upgradeCount * 16 - 1)
				.get());
		}
	}

	@Override
	public ItemStack getWailaStack (IDataAccessor accessor) {
		TileEntity te = accessor.getTileEntity();
		return getItemStack((SoulInquirerTileEntity) te, 1, 0);
	}
}
