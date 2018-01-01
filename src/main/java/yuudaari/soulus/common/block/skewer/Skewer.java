package yuudaari.soulus.common.block.skewer;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
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
import yuudaari.soulus.common.block.UpgradeableBlock;
import yuudaari.soulus.common.item.BloodCrystal;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.Soulus;

public class Skewer extends UpgradeableBlock<SkewerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		BLOOD_CRYSTAL(0, "blood_crystal", ModItems.BLOOD_CRYSTAL.getItemStack()), DAMAGE(1, "damage",
				new ItemStack(Items.QUARTZ))
		//, POISON(2, "poison",
		//		new ItemStack(Items.SPIDER_EYE)), POWER(3, "power", new ItemStack(Blocks.REDSTONE_TORCH))
		;

		private final int index;
		private final String name;
		private final ItemStack stack;
		// by default all upgrades are capped at 16
		private Integer maxQuantity;

		private Upgrade(int index, String name, ItemStack item) {
			this.index = index;
			this.name = name;
			this.stack = item;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getMaxQuantity() {
			// all upgrades by default are capped at 16
			if (maxQuantity == null) {
				if (name == "blood_crystal")
					return 1;
				if (name == "damage")
					return 256;
				if (name == "poison")
					return 16;
				if (name == "power")
					return 1;
			}

			return maxQuantity;
		}

		@Override
		public void setMaxQuantity(int quantity) {
			maxQuantity = quantity;
		}

		@Override
		public boolean isItemStack(ItemStack stack) {
			if (stack.getItem() != this.stack.getItem())
				return false;

			if (name == "blood_crystal")
				return !BloodCrystal.isFilled(stack);

			return true;
		}

		@Override
		public ItemStack getItemStack(int quantity) {
			return new ItemStack(this.stack.getItem(), quantity);
		}

		@Override
		public ItemStack getItemStackForTileEntity(UpgradeableBlockTileEntity te, int quantity) {
			ItemStack stack = getItemStack(quantity);

			if (name == "blood_crystal") {
				SkewerTileEntity ste = (SkewerTileEntity) te;
				BloodCrystal.setContainedBlood(stack,
						Math.min(BloodCrystal.INSTANCE.requiredBlood, ste.bloodCrystalBlood));
			}

			return stack;
		}
	}

	@Override
	public IUpgrade[] getUpgrades() {
		return Upgrade.values();
	}

	/////////////////////////////////////////
	// Serializer
	//

	@Override
	public Class<? extends UpgradeableBlock<SkewerTileEntity>> getSerializationClass() {
		return Skewer.class;
	}

	public float baseDamage = 1;
	public float upgradeDamageEffectiveness = 0.04f;
	public int bloodPerDamage = 1;
	public double chanceForBloodPerHit = 0.5;

	{
		serializer.fields.addAll(Arrays.asList("baseDamage", "upgradeDamageEffectiveness", "bloodPerDamage"));
	}

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool EXTENDED = PropertyBool.create("extended");

	public Skewer() {
		super("skewer", new Material(MapColor.GRAY));
		setHasItem();
		setDefaultState(getDefaultState().withProperty(EXTENDED, false).withProperty(FACING, EnumFacing.NORTH));
		setHarvestLevel("pickaxe", 1);
		setHardness(3F);
	}

	public static Skewer INSTANCE = new Skewer();

	@Override
	public UpgradeableBlock<SkewerTileEntity> getInstance() {
		return INSTANCE;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		int power = world.isBlockIndirectlyGettingPowered(pos);

		EnumFacing facing = state.getValue(FACING);

		BlockPos spikePos = pos.offset(facing);
		Block blockAtSpikePos = world.getBlockState(spikePos).getBlock();

		boolean spikeBlocked = !blockAtSpikePos.equals(Blocks.AIR) && !blockAtSpikePos.isReplaceable(world, spikePos);

		boolean shouldBeExtended = power > 0 && !spikeBlocked;

		if (state.getValue(EXTENDED) != shouldBeExtended) {

			world.setBlockState(pos,
					getDefaultState().withProperty(FACING, facing).withProperty(EXTENDED, shouldBeExtended), 11);

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F,
					world.rand.nextFloat() * 0.25F + 0.6F);

		}
	}

	/////////////////////////////////////////
	// Blockstate
	//

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty<?>[] { FACING, EXTENDED });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(EXTENDED, (meta & 1) == 0 ? false : true).withProperty(FACING,
				EnumFacing.getFront(meta / 2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() * 2 + (state.getValue(EXTENDED) ? 1 : 0);
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		int power = world.isBlockIndirectlyGettingPowered(pos);
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
				.withProperty(EXTENDED, power > 0);
	}

	/////////////////////////////////////////
	// Tile Entity
	//
	@Override
	public boolean hasTileEntity(IBlockState blockState) {
		return true;
	}

	@Override
	public Class<? extends UpgradeableBlockTileEntity> getTileEntityClass() {
		return SkewerTileEntity.class;
	}

	@Override
	public UpgradeableBlockTileEntity createTileEntity(World worldIn, IBlockState blockState) {
		return new SkewerTileEntity();
	}

	/////////////////////////////////////////
	// Collision
	//

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {

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

	public AxisAlignedBB bottomBox(EnumFacing facing, BlockPos pos) {
		return contract(getSpikeHitbox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB topBox(EnumFacing facing, BlockPos pos) {
		return offset(bottomBox(facing, pos), facing, 0.69);
	}

	public AxisAlignedBB offset(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.offset(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB expand(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.expand(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public AxisAlignedBB contract(AxisAlignedBB box, EnumFacing facing, double n) {
		return box.contract(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	public static AxisAlignedBB getSpikeHitbox(EnumFacing facing, BlockPos pos) {
		AxisAlignedBB result = new AxisAlignedBB(pos.offset(facing));
		double topPadding = 0.3;
		double sidePadding = 0.2;
		return result
				.contract(facing.getFrontOffsetX() == 1 ? topPadding : sidePadding * 2,
						facing.getFrontOffsetY() == 1 ? topPadding : sidePadding * 2,
						facing.getFrontOffsetZ() == 1 ? topPadding : sidePadding * 2)
				.offset(facing.getFrontOffsetX() == 1 ? 0 : sidePadding,
						facing.getFrontOffsetY() == 1 ? 0 : sidePadding,
						facing.getFrontOffsetZ() == 1 ? 0 : sidePadding);
	}

	/////////////////////////////////////////
	// Waila
	//

	@Optional.Method(modid = "waila")
	@SideOnly(Side.CLIENT)
	@Override
	protected void onWailaTooltipHeader(List<String> currentTooltip, IBlockState blockState, SkewerTileEntity te,
			boolean isSneaking) {

		currentTooltip.add(I18n.format("waila." + Soulus.MODID
				+ (blockState.getValue(Skewer.EXTENDED) ? ":skewer.extended" : ":skewer.not_extended")));

		if (te.upgrades.get(Upgrade.BLOOD_CRYSTAL) == 1) {
			currentTooltip.add(I18n.format("waila." + Soulus.MODID + ":skewer.blood_crystal_stored_blood",
					te.bloodCrystalBlood, BloodCrystal.INSTANCE.requiredBlood));
		}
	}

}