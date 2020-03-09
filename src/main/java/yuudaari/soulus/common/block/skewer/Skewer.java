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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSkewer;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.item.SoulCatalyst;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.ItemStackMutable;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class Skewer extends UpgradeableBlock<SkewerTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {

		CRYSTAL_BLOOD (0, "crystal_blood", ItemRegistry.CRYSTAL_BLOOD),
		DAMAGE (1, "damage", Items.QUARTZ),
		POISON (2, "poison", Items.SPIDER_EYE),
		POWER (3, "power", Item.getItemFromBlock(Blocks.REDSTONE_TORCH)),
		TETHER (4, "tether", ItemRegistry.ASH),
		PLAYER (5, "player", ItemRegistry.SOUL_CATALYST),
		LOOTING (6, "looting", Items.EXPERIENCE_BOTTLE);

		private final int index;
		private final String name;
		private final Item item;
		private Integer maxQuantity;

		private Upgrade (final int index, final String name, final Item item) {
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
				switch (name) {
					case "crystal_blood":
						return 1;

					case "damage":
						return 64;

					case "poison":
						return 16;

					case "power":
						return 1;

					case "tether":
						return 16;

					case "player":
						return 1;

					case "looting":
						return 64;
				}
			}

			return maxQuantity;
		}

		@Override
		public boolean canOverrideMaxQuantity () {
			return !name.equals("crystal_blood") && !name.equals("player");
		}

		@Override
		public boolean canSwitchOut () {
			return name.equals("crystal_blood");
		}

		@Override
		public void setMaxQuantity (int quantity) {
			maxQuantity = quantity;
		}

		@Override
		public boolean isItemStack (ItemStack stack) {
			if (!IUpgrade.super.isItemStack(stack))
				return false;

			if (this == Upgrade.CRYSTAL_BLOOD)
				return !CrystalBlood.isFilled(stack);

			if (this == Upgrade.PLAYER)
				return SoulCatalyst.isFilled(stack);

			return true;
		}

		@Override
		public boolean isItemStackForTileEntity (final ItemStack stack, final UpgradeableBlockTileEntity te) {
			if (!IUpgrade.super.isItemStackForTileEntity(stack, te))
				return false;

			if (this == Upgrade.LOOTING)
				return te.upgrades.get(Upgrade.PLAYER) > 0;

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			final ItemStack stack = IUpgrade.super.getItemStack(quantity);

			if (this == Upgrade.PLAYER)
				SoulCatalyst.setFilled(stack);

			return stack;
		}

		@Override
		public ItemStack getItemStackForTileEntity (UpgradeableBlockTileEntity te, int quantity) {
			ItemStack stack = getItemStack(quantity);

			if (this == Upgrade.CRYSTAL_BLOOD) {
				SkewerTileEntity ste = (SkewerTileEntity) te;
				CrystalBlood.setContainedBlood(stack, Math
					.min(CrystalBlood.CONFIG.requiredBlood, ste.crystalBloodContainedBlood));
			}

			return stack;
		}

		@Override
		public boolean isSecret (final UpgradeableBlockTileEntity te) {
			final SkewerTileEntity ste = (SkewerTileEntity) te;
			return this == Upgrade.LOOTING && ste.upgrades.get(Upgrade.PLAYER) <= 0;
		}
	}

	@Override
	public IUpgrade[] getUpgrades () {
		return Upgrade.values();
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigSkewer CONFIG;

	/////////////////////////////////////////
	// Properties
	//

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool EXTENDED = PropertyBool.create("extended");

	public Skewer () {
		super("skewer", new Material(MapColor.GRAY));
		setHasItem();
		setDefaultState(getDefaultState().withProperty(EXTENDED, false).withProperty(FACING, EnumFacing.NORTH));
		setTool(Tool.PICK, 1);
		setSoundType(SoundType.STONE);
		setHardness(3F);
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<SkewerTileEntity> getInstance () {
		return BlockRegistry.SKEWER;
	}

	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean hasComparatorInputOverride (IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride (final IBlockState state, final World world, final BlockPos pos) {
		SkewerTileEntity te = (SkewerTileEntity) world.getTileEntity(pos);
		return te == null ? 0 : te.getSignalStrength();
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public List<ItemStack> onBlockDestroy (final World world, final BlockPos pos, final int fortune, final EntityPlayer player) {
		final List<ItemStack> drops = super.onBlockDestroy(world, pos, fortune, player);

		final ItemStack bloodCrystal = drops.stream().filter(CrystalBlood::isFilled).findFirst().orElse(ItemStack.EMPTY);
		if (!world.isRemote && !bloodCrystal.isEmpty())
			bloodCrystal.getItem().onCreated(bloodCrystal, world, player);

		return drops;
	}

	@Override
	public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		SkewerTileEntity te = (SkewerTileEntity) world.getTileEntity(pos);
		if (te == null) return;

		if (placer instanceof EntityPlayer) {
			te.setOwner((EntityPlayer) placer);
		}
	}

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

	@Override
	public boolean returnPreviousStackAfterInsert (final World world, final BlockPos pos, final EntityPlayer player, final ItemStackMutable slot, final ItemStack result) {
		if (!super.returnPreviousStackAfterInsert(world, pos, player, slot, result))
			return false;

		if (CrystalBlood.isFilled(result))
			result.getItem().onCreated(result, world, player);

		return true;
	}

	@Override
	public void onReturningUpgradesToPlayer (final World world, final BlockPos pos, final EntityPlayer player, final List<ItemStack> returning) {
		for (final ItemStack stack : returning)
			if (CrystalBlood.isFilled(stack))
				stack.getItem().onCreated(stack, world, player);

		final SkewerTileEntity ste = (SkewerTileEntity) world.getTileEntity(pos);
		if (ste.upgrades.get(Upgrade.LOOTING) > 0 && ste.upgrades.get(Upgrade.PLAYER) <= 0)
			Upgrade.LOOTING.addItemStackToList(ste, returning, ste.removeUpgrade(Upgrade.LOOTING));
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
					if (entityBox.intersects(getUpwardsCollisionBoxBottom(facing, pos)) && //
						entityBox.intersects(getUpwardsCollisionBoxTop(facing, pos))) {

						collidingBoxes.add(box);
					}

				} else if (facing.getAxis() != Axis.Y) {
					if (entityBox.intersects(getHorizontalCollisionBoxTop(facing, pos)) || //
						entityBox.intersects(getHorizontalCollisionBoxSideLeft(facing, pos)) ||  //
						entityBox.intersects(getHorizontalCollisionBoxSideRight(facing, pos))) {

						collidingBoxes.add(box);
					}
				}
			}
		}
	}

	public static AxisAlignedBB getSpikeHitbox (final EnumFacing facing, final BlockPos pos) {
		final AxisAlignedBB result = new AxisAlignedBB(pos.offset(facing));
		final double topPadding = 0.3;
		final double sidePadding = 0.2;
		return result
			.contract( //
				facing.getFrontOffsetX() == 1 ? topPadding : sidePadding * 2, //
				facing.getFrontOffsetY() == 1 ? topPadding : sidePadding * 2, //
				facing.getFrontOffsetZ() == 1 ? topPadding : sidePadding * 2)
			.offset( //
				facing.getFrontOffsetX() == 1 ? 0 : sidePadding, // 
				facing.getFrontOffsetY() == 1 ? 0 : sidePadding, //
				facing.getFrontOffsetZ() == 1 ? 0 : sidePadding);
	}

	private AxisAlignedBB getHorizontalCollisionBoxTop (final EnumFacing facing, final BlockPos pos) {
		return contract(contract( //
			new AxisAlignedBB(pos.offset(facing).up(2)), //
			EnumFacing.UP, 0.5), //
			EnumFacing.DOWN, 0.25);
	}

	private AxisAlignedBB getHorizontalCollisionBoxSideLeft (final EnumFacing facing, final BlockPos pos) {
		EnumFacing side = facing.rotateYCCW();
		return contract(new AxisAlignedBB(pos.offset(facing).offset(side)), side.getOpposite(), 0.4);
	}

	private AxisAlignedBB getHorizontalCollisionBoxSideRight (final EnumFacing facing, final BlockPos pos) {
		EnumFacing side = facing.rotateY();
		return contract(new AxisAlignedBB(pos.offset(facing).offset(side)), side.getOpposite(), 0.4);
	}

	private AxisAlignedBB getUpwardsCollisionBoxBottom (final EnumFacing facing, final BlockPos pos) {
		return contract(getSpikeHitbox(facing, pos), facing, 0.69);
	}

	private AxisAlignedBB getUpwardsCollisionBoxTop (final EnumFacing facing, final BlockPos pos) {
		return offset(getUpwardsCollisionBoxBottom(facing, pos), facing, 0.69);
	}

	// private AxisAlignedBB getDownwardsCollisionBoxBottom (final EnumFacing facing, final BlockPos pos) {
	// 	return contract(getSpikeHitbox(facing, pos), facing, 0.69);
	// }

	// private AxisAlignedBB getDownwardsCollisionBoxTop (final EnumFacing facing, final BlockPos pos) {
	// 	return offset(getDownwardsCollisionBoxBottom(facing, pos), facing, 1);
	// }

	// private AxisAlignedBB getDownwardsCollisionBoxInner (final EnumFacing facing, final BlockPos pos) {
	// 	return contract(getSpikeHitbox(facing, pos), facing, 0.4);
	// }

	// private AxisAlignedBB[] getDownwardsCollisionBoxesOuter (final EnumFacing facing, final BlockPos pos) {
	// 	return new AxisAlignedBB[] {
	// 		(offset(contract(getSpikeHitbox(facing, pos), facing, 0.4), EnumFacing.EAST, 1)),
	// 		(offset(contract(getSpikeHitbox(facing, pos), facing, 0.4), EnumFacing.WEST, 1)),
	// 		(offset(contract(getSpikeHitbox(facing, pos), facing, 0.4), EnumFacing.NORTH, 1)),
	// 		(offset(contract(getSpikeHitbox(facing, pos), facing, 0.4), EnumFacing.SOUTH, 1)),
	// 	};
	// }

	private AxisAlignedBB offset (final AxisAlignedBB box, final EnumFacing facing, final double n) {
		return box.offset(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	private AxisAlignedBB contract (final AxisAlignedBB box, final EnumFacing facing, final double n) {
		return box.contract(facing.getFrontOffsetX() * n, facing.getFrontOffsetY() * n, facing.getFrontOffsetZ() * n);
	}

	/////////////////////////////////////////
	// Waila
	//

	@Override
	protected void onWailaTooltipHeader (List<String> currentTooltip, IBlockState blockState, SkewerTileEntity te, EntityPlayer player) {

		currentTooltip.add(Translation.localize("waila." + Soulus.MODID + (blockState
			.getValue(Skewer.EXTENDED) ? ":skewer.extended" : ":skewer.not_extended")));

		if (te.upgrades.get(Upgrade.CRYSTAL_BLOOD) == 1) {
			currentTooltip.add(new Translation("waila." + Soulus.MODID + ":skewer.crystal_blood_stored_blood")
				.addArgs(te.crystalBloodContainedBlood, CrystalBlood.CONFIG.requiredBlood)
				.get());
		}
	}

}
