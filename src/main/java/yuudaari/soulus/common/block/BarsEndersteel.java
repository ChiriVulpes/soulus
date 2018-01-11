package yuudaari.soulus.common.block;

import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlockPane;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/*
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

import yuudaari.soulus.common.CreativeTab;
*/
public class BarsEndersteel extends ModBlockPane {
	public static final IProperty<EndersteelType> VARIANT = PropertyEnum.create("variant", EndersteelType.class);

	public BarsEndersteel() {
		super("bars_endersteel", new Material(MapColor.GRASS));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		//setDefaultState(getDefaultState().withProperty(VARIANT, EndersteelType.NORMAL));
		setHasDescription();
	}

	/*
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		return world.setBlockState(pos, state.cycleProperty(VARIANT));
	}
	
	/*
	@Override
	public void getSubBlocks(CreativeTab tab, NonNullList<ItemStack> list) {
		for (final EndersteelType enumType : EndersteelType.values()) {
			list.add(new ItemStack(this, 1, enumType.getMeta()));
		}
	}
	
	@Override
	public void registerItemModel() {
		NonNullList<ItemStack> stacks = NonNullList.create();
		getSubBlocks(CreativeTab.INSTANCE, stacks);
		for (final ItemStack stack : stacks) {
	
			ModelLoader.setCustomModelResourceLocation(this.getItemBlock(), stack.getMetadata(),
					new ModelResourceLocation(this.getRegistryName(),
							"inventory;" + VARIANT.getName() + "=" + EndersteelType.byMetadata(stack.getMetadata())));
		}
	}
	*/
}
