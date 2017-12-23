package yuudaari.soulus.common.block.summoner;

import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.util.MobTarget;
import yuudaari.soulus.common.util.ModBlock;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class SummonerEmpty extends ModBlock {

	public static final IProperty<EndersteelType> VARIANT = PropertyEnum.create("variant", EndersteelType.class);

	public SummonerEmpty() {
		super("summoner_empty", new Material(MapColor.STONE).setTransparent());
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		setDefaultState(getDefaultState().withProperty(VARIANT, EndersteelType.NORMAL));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		List<IProperty<?>> props = new ArrayList<>(super.createBlockState().getProperties());
		props.add(VARIANT);
		return new BlockStateContainer(this, props.toArray(new IProperty<?>[0]));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, EndersteelType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTab tab, NonNullList<ItemStack> list) {
		for (EndersteelType enumType : EndersteelType.values()) {
			list.add(new ItemStack(this, 1, enumType.getMeta()));
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		ItemBlock result = new ItemBlock(this) {
			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};

		result.setRegistryName(getRegistryName());

		return result;
	}

	@Override
	public void registerItemModel() {
		NonNullList<ItemStack> stacks = NonNullList.create();
		getSubBlocks(CreativeTab.INSTANCE, stacks);
		for (ItemStack stack : stacks) {
			ModelLoader.setCustomModelResourceLocation(super.getItemBlock(), stack.getMetadata(),
					new ModelResourceLocation(this.getRegistryName(), VARIANT.getName() + "="
							+ EndersteelType.byMetadata(stack.getMetadata()).getName().toLowerCase()));
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem == null || heldItem.getItem() != ModItems.SOULBOOK)
			return false;

		String mobTarget = MobTarget.getMobTarget(heldItem);
		int containedEssence = Soulbook.getContainedEssence(heldItem);
		if (mobTarget == null || containedEssence < Soulus.config.getSoulbookQuantity(mobTarget))
			return false;

		IBlockState mobSpawner = ((Summoner) ModBlocks.SUMMONER).getDefaultState();
		world.setBlockState(pos, mobSpawner);

		SummonerTileEntity tileEntity = (SummonerTileEntity) world.getTileEntity(pos);
		if (tileEntity == null) {
			throw new RuntimeException("Mob spawner tile entity was not created. Something went wrong.");
		}

		tileEntity.setMob(mobTarget);
		player.inventory.removeStackFromSlot(player.inventory.currentItem);

		return true;
	}
}