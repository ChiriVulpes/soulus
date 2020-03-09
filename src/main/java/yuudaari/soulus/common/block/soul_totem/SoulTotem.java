package yuudaari.soulus.common.block.soul_totem;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSoulTotem;
import yuudaari.soulus.common.item.SoulCatalyst;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.StructureMap;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;
import java.util.List;

@ConfigInjected(Soulus.MODID)
public class SoulTotem extends UpgradeableBlock<SoulTotemTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {

		SOUL_CATALYST (0, "soul_catalyst", ItemRegistry.SOUL_CATALYST),
		EFFICIENCY (1, "efficiency", ItemRegistry.GEAR_NIOBIUM);

		private final int index;
		private final String name;
		private final Item item;
		private Integer maxQuantity;

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
			if (maxQuantity == null) {
				if (name.equals("soul_catalyst"))
					return 1;

				if (name.equals("efficiency"))
					return 16;
			}

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

			if (name.equals("soul_catalyst"))
				return SoulCatalyst.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = IUpgrade.super.getItemStack(quantity);

			if (name.equals("soul_catalyst"))
				SoulCatalyst.setFilled(stack);

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

	@Inject public static ConfigSoulTotem CONFIG;

	public SoulTotem () {
		super("soul_totem", new Material(MapColor.BLUE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setTool(Tool.PICK, 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setHasDescription();
		setDefaultState(getDefaultState().withProperty(CONNECTED, false));
	}

	@Override
	public UpgradeableBlock<SoulTotemTileEntity> getInstance () {
		return BlockRegistry.SOUL_TOTEM;
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride (IBlockState state, World world, BlockPos pos) {
		SoulTotemTileEntity te = (SoulTotemTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.getSignalStrength();
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {

		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));

		if (state.getValue(CONNECTED)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(-.4375, -.875, -.4375, 1.4375, .25, 0));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(-.4375, -.875, 1, 1.4375, .25, 1.4375));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(-.4375, -.875, 0, 0, .25, 1));
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(1, -.875, 0, 1.4375, .25, 1));
		}
	}

	/////////////////////////////////////////
	// Blockstate
	//

	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			CONNECTED
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(CONNECTED, meta == 0 ? false : true);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(CONNECTED) ? 1 : 0;
	}

	/////////////////////////////////////////
	// Structure
	//

	public StructureMap structure = new StructureMap();
	{
		// BlockValidator bars = BlockValidator.byBlock(ModBlocks.BARS_ENDERSTEEL);
		BlockValidator niobium = BlockValidator.byBlock(BlockRegistry.BLOCK_NIOBIUM);

		// layer -1
		structure.addBlock(-1, -1, -1, niobium);
		// structure.addBlock(0, -1, -1, bars);
		structure.addBlock(1, -1, -1, niobium);

		// structure.addBlock(-1, -1, 0, bars);
		// structure.addBlock(1, -1, 0, bars);

		structure.addBlock(-1, -1, 1, niobium);
		// structure.addBlock(0, -1, 1, bars);
		structure.addBlock(1, -1, 1, niobium);
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
		return SoulTotemTileEntity.class;
	}

	@Override
	public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		SoulTotemTileEntity te = (SoulTotemTileEntity) world.getTileEntity(pos);
		if (te == null) return;

		if (placer instanceof EntityPlayer) {
			te.setOwner((EntityPlayer) placer);
		}
	}

	/////////////////////////////////////////
	// Waila
	//

	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, SoulTotemTileEntity te, EntityPlayer player) {

		currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":soul_totem.fuel_percentage", (int) Math
			.ceil(te.getFuelPercent() * 100)));
	}
}
