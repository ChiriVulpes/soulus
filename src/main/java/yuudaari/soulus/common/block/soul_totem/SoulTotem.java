package yuudaari.soulus.common.block.soul_totem;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSoulTotem;
import yuudaari.soulus.common.util.Material;

@ConfigInjected(Soulus.MODID)
public class SoulTotem extends UpgradeableBlock<SoulTotemTileEntity> {

	/////////////////////////////////////////
	// Upgrades
	//

	public static enum Upgrade implements IUpgrade {
		SOUL_CATALYST (0, "soul_catalyst", ModItems.SOUL_CATALYST.getItemStack()),
		EFFICIENCY (1, "efficiency", ModItems.GEAR_NIOBIUM.getItemStack());

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

			return true;
		}

		@Override
		public ItemStack getItemStack (int quantity) {
			ItemStack stack = new ItemStack(this.stack.getItem(), quantity);
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

	@Inject(ConfigSoulTotem.class) public static ConfigSoulTotem CONFIG;

	public SoulTotem () {
		super("soul_totem", new Material(MapColor.BLUE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		disableStats();
		setHasDescription();
	}

	@Override
	public UpgradeableBlock<SoulTotemTileEntity> getInstance () {
		return ModBlocks.SOUL_TOTEM;
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
	public UpgradeableBlockTileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new SoulTotemTileEntity();
	}
}
