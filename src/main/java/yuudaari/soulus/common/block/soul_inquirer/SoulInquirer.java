package yuudaari.soulus.common.block.soul_inquirer;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.LangHelper;
import yuudaari.soulus.common.util.Material;
import java.util.Collections;
import java.util.List;

@ConfigInjected(Soulus.MODID)
public class SoulInquirer extends UpgradeableBlock<SoulInquirerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		RANGE (0, "range", ModItems.ORB_MURKY.getItemStack()),
		COUNT (1, "count", ModItems.CRYSTAL_BLOOD.getItemStack());

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

			if (name == "count") {
				return CrystalBlood.isFilled(stack);
			} else if (name == "range") {
				return OrbMurky.isFilled(stack);
			}

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = new ItemStack(this.stack.getItem(), quantity);

			if (name == "count") {
				CrystalBlood.setFilled(stack);
			} else if (name == "range") {
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
		return ModBlocks.SOUL_INQUIRER;
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

	public static class SoulInquirerItemBlock extends ItemBlock {

		public SoulInquirerItemBlock (Block b) {
			super(b);
			setRegistryName(Soulus.MODID + ":soul_inquirer");
		}

		@Override
		public String getItemStackDisplayName (ItemStack stack) {
			String essenceType = EssenceType.getEssenceType(stack);
			ConfigEssence config = CONFIG_ESSENCES.get(essenceType);
			if (essenceType == null || config == null)
				return LangHelper.localize(this.getUnlocalizedName() + ".unfocused.name").trim();

			String alignment = config.name;
			if (alignment == null) {
				String translationKey = "entity." + essenceType + ".name";
				alignment = LangHelper.localize(translationKey);
				if (translationKey.equals(alignment)) {
					alignment = LangHelper.localize("entity." + EntityList
						.getTranslationName(new ResourceLocation(essenceType)) + ".name");
				}
			}

			return LangHelper.localize(this.getUnlocalizedName() + ".focused.name", alignment).trim();
		}
	}

	private final SoulInquirerItemBlock ITEM = new SoulInquirerItemBlock(this);

	@Override
	public ItemBlock getItemBlock () {
		return ITEM;
	}

	public ItemStack getItemStack (SoulInquirerTileEntity te, int count, int metadata) {
		ItemStack itemStack = new ItemStack(ITEM, count, metadata);

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
	public boolean onActivateInsert (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		Item item = stack.getItem();
		IBlockState state = world.getBlockState(pos);


		// try to insert a soulbook
		if (item == ModItems.SOULBOOK) {
			if (!Soulbook.isFilled(stack))
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

			String newEssenceType = EssenceType.getEssenceType(stack);
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
			currentTooltip.add(LangHelper.localize("waila." + Soulus.MODID + ":soul_inquirer.detecting_" + detection));
		} else {
			currentTooltip.add(LangHelper
				.localize("waila." + Soulus.MODID + ":soul_inquirer.detecting", Math
					.min(15, te.soulInquiry()), upgradeCount * 16 - 1));
		}
	}

	@Override
	public ItemStack getWailaStack (IDataAccessor accessor) {
		TileEntity te = accessor.getTileEntity();
		return getItemStack((SoulInquirerTileEntity) te, 1, 0);
	}
}
