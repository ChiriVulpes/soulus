package yuudaari.soulus.common.block.skewer;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSkewer;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.Soulus;

@ConfigInjected(Soulus.MODID)
public class Skewer extends UpgradeableBlock<SkewerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		CRYSTAL_BLOOD (0, "crystal_blood", ModItems.CRYSTAL_BLOOD.getItemStack()),
		DAMAGE (1, "damage", new ItemStack(Items.QUARTZ)),
		POISON (2, "poison", new ItemStack(Items.SPIDER_EYE)),
		POWER (3, "power", new ItemStack(Blocks.REDSTONE_TORCH)),
		TETHER (4, "tether", ModItems.ASH.getItemStack()),
		PLAYER (5, "player", ModItems.SOUL_CATALYST.getItemStack());

		private final int index;
		private final String name;
		private final ItemStack stack;
		// by default all upgrades are capped at 16
		private Integer maxQuantity;

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
			if (maxQuantity == null) {
				if (name.equals("crystal_blood"))
					return 1;
				if (name.equals("damage"))
					return 64;
				if (name.equals("poison"))
					return 16;
				if (name.equals("power"))
					return 1;
				if (name.equals("tether"))
					return 16;
				if (name.equals("player"))
					return 1;
			}

			return maxQuantity;
		}

		@Override
		public boolean canOverrideMaxQuantity () {
			return !name.equals("crystal_blood") && !name.equals("player");
		}

		@Override
		public void setMaxQuantity (int quantity) {
			maxQuantity = quantity;
		}

		@Override
		public boolean isItemStack (ItemStack stack) {
			if (stack.getItem() != this.stack.getItem())
				return false;

			if (name == "crystal_blood")
				return !CrystalBlood.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			return new ItemStack(this.stack.getItem(), quantity);
		}

		@Override
		public ItemStack getItemStackForTileEntity (UpgradeableBlockTileEntity te, int quantity) {
			ItemStack stack = getItemStack(quantity);

			if (name == "crystal_blood") {
				SkewerTileEntity ste = (SkewerTileEntity) te;
				CrystalBlood.setContainedBlood(stack, Math
					.min(CrystalBlood.CONFIG.requiredBlood, ste.crystalBloodContainedBlood));
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

	@Inject(ConfigSkewer.class) public static ConfigSkewer CONFIG;

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool EXTENDED = PropertyBool.create("extended");

	public Skewer () {
		super("skewer", new Material(MapColor.GRAY));
		setHasItem();
		setDefaultState(getDefaultState().withProperty(EXTENDED, false).withProperty(FACING, EnumFacing.NORTH));
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.STONE);
		setHardness(3F);
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<SkewerTileEntity> getInstance () {
		return ModBlocks.SKEWER;
	}

	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void neighborChanged (IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		updateExtendedState(state, world, pos);
	}

	public void updateExtendedState (IBlockState state, World world, BlockPos pos) {
		int power = world.isBlockIndirectlyGettingPowered(pos);

		EnumFacing facing = state.getValue(FACING);

		BlockPos spikePos = pos.offset(facing);
		Block blockAtSpikePos = world.getBlockState(spikePos).getBlock();

		boolean spikeBlocked = !blockAtSpikePos.equals(Blocks.AIR) && !blockAtSpikePos.isReplaceable(world, spikePos);

		boolean shouldBeExtended = power > 0 && !spikeBlocked;

		TileEntity te = world.getTileEntity(pos);

		if (te != null && te instanceof SkewerTileEntity && //
			((SkewerTileEntity) te).upgrades.get(Upgrade.POWER) > 0) {
			shouldBeExtended = !shouldBeExtended;
		}

		if (state.getValue(EXTENDED) != shouldBeExtended) {

			world.setBlockState(pos, getDefaultState().withProperty(FACING, facing)
				.withProperty(EXTENDED, shouldBeExtended), 11);

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand
				.nextFloat() * 0.25F + 0.6F);

		}
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty<?>[] {
			FACING, EXTENDED
		});
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(EXTENDED, (meta & 1) == 0 ? false : true)
			.withProperty(FACING, EnumFacing.getFront(meta / 2));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(FACING).getIndex() * 2 + (state.getValue(EXTENDED) ? 1 : 0);
	}

	public IBlockState withRotation (IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror (IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		int power = world.isBlockIndirectlyGettingPowered(pos);
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
			.withProperty(EXTENDED, power > 0);
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
		return SkewerTileEntity.class;
	}

	@Override
	public UpgradeableBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new SkewerTileEntity();
	}

	/////////////////////////////////////////
	// Collision
	//

	@Override
	public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {

		addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));

		if (state.getValue(EXTENDED)) {
			EnumFacing facing = state.getValue(FACING);
			AxisAlignedBB box = getSpikeHitbox(facing, pos);

			if (entityBox.intersects(box)) {
				if (facing == EnumFacing.UP) {
					if (entityBox.intersects(bottomBox(facing, pos)) && entityBox.intersects(topBox(facing, pos))) {
						collidingBoxes.add(box);
					}
				}
			}

		}
	}

	public AxisAlignedBB bottomBox (EnumFacing facing, BlockPos pos) {
		return contract(getSpikeHitbox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB topBox (EnumFacing facing, BlockPos pos) {
		return offset(bottomBox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB offset (AxisAlignedBB box, EnumFacing facing, double n) {
		return box.offset(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB expand (AxisAlignedBB box, EnumFacing facing, double n) {
		return box.expand(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB contract (AxisAlignedBB box, EnumFacing facing, double n) {
		return box.contract(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public static AxisAlignedBB getSpikeHitbox (EnumFacing facing, BlockPos pos) {
		AxisAlignedBB result = new AxisAlignedBB(pos.offset(facing));
		double topPadding = 0.3;
		double sidePadding = 0.2;
		return result
			.contract(facing.getFrontOffsetX() == 1 ? topPadding : sidePadding * 2, facing
				.getFrontOffsetY() == 1 ? topPadding : sidePadding * 2, facing
					.getFrontOffsetZ() == 1 ? topPadding : sidePadding * 2)
			.offset(facing.getFrontOffsetX() == 1 ? 0 : sidePadding, facing
				.getFrontOffsetY() == 1 ? 0 : sidePadding, facing.getFrontOffsetZ() == 1 ? 0 : sidePadding);
	}

	/////////////////////////////////////////
	// Waila
	//

	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, SkewerTileEntity te, boolean isSneaking) {

		currentTooltip.add(I18n.format("waila." + Soulus.MODID + (blockState
			.getValue(Skewer.EXTENDED) ? ":skewer.extended" : ":skewer.not_extended")));

		if (te.upgrades.get(Upgrade.CRYSTAL_BLOOD) == 1) {
			currentTooltip.add(I18n
				.format("waila." + Soulus.MODID + ":skewer.crystal_blood_stored_blood", te.crystalBloodContainedBlood, CrystalBlood.CONFIG.requiredBlood));
		}
	}

}
