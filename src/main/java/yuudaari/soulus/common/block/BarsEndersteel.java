package yuudaari.soulus.common.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSummoner;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Material;

@ConfigInjected(Soulus.MODID)
public class BarsEndersteel extends Registration.BlockPane {

	@Inject public static ConfigSummoner CONFIG;

	public static final IProperty<EndersteelType> VARIANT = PropertyEnum.create("variant", EndersteelType.class);

	public BarsEndersteel () {
		super("bars_endersteel", new Material(MapColor.GRASS));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		setDefaultState(getDefaultState().withProperty(VARIANT, EndersteelType.NORMAL));
		setHasDescription();
		registerWailaProvider(BarsEndersteel.class);
	}

	@Override
	protected BlockStateContainer createBlockState () {
		List<IProperty<?>> props = new ArrayList<>(super.createBlockState().getProperties());
		props.add(VARIANT);
		return new BlockStateContainer(this, props.toArray(new IProperty<?>[0]));
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return getDefaultState().withProperty(VARIANT, EndersteelType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(VARIANT).getMeta();
	}

	@Override
	public int damageDropped (IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks (CreativeTabs tab, NonNullList<ItemStack> list) {
		for (final EndersteelType enumType : EndersteelType.values()) {
			list.add(new ItemStack(this, 1, enumType.getMeta()));
		}
	}

	@Override
	public Registration.ItemBlock createItemBlock () {
		return new Registration.ItemBlock(this) {

			@Override
			public int getMetadata (int damage) {
				return damage;
			}

			@SideOnly(Side.CLIENT)
			@Override
			public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(Translation.localize("tooltip." + Soulus.MODID + ":summoner.style." + EndersteelType
					.byMetadata(stack.getItemDamage())
					.getName()));
			}
		}
			.setHasSubtypes(true);
	}

	@Override
	public void registerItemModel () {
		NonNullList<ItemStack> stacks = NonNullList.create();
		getSubBlocks(CreativeTab.INSTANCE, stacks);
		for (final ItemStack stack : stacks) {
			final String variantName = EndersteelType.byMetadata(stack.getMetadata()).getName();
			final ModelResourceLocation model = new ModelResourceLocation(this.getRegistryName() + "/" + variantName, "inventory;" + VARIANT.getName() + "=" + variantName);
			ModelLoader.setCustomModelResourceLocation(this.getItemBlock(), stack.getMetadata(), model);
		}
	}

	@Override
	public void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
		registry.registerAll(CONFIG.styleItems.entrySet()
			.stream()
			.map(item -> {
				final List<Object> recipe = new ArrayList<>();
				recipe.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getKey())));
				recipe.addAll(Collections.nCopies(8, getIngredientFromStacksOfOtherEndersteelTypes(item.getValue())));

				return new ShapelessOreRecipe(null, getStackFromEndersteelType(item.getValue()), recipe.toArray(new Object[0]))
					.setRegistryName(getRegistryName() + "_" + item.getValue().getName().toLowerCase());
			})
			.toArray(ShapelessOreRecipe[]::new));
	}

	public ItemStack getStackFromEndersteelType (final EndersteelType type) {
		return getItemStack(1, getMetaFromState(getDefaultState().withProperty(VARIANT, type)));
	}

	private Ingredient getIngredientFromStacksOfOtherEndersteelTypes (final EndersteelType type) {
		return Ingredient.fromStacks(Arrays.stream(EndersteelType.values())
			.filter(e -> e != type)
			.map(e -> getStackFromEndersteelType(e))
			.toArray(ItemStack[]::new));
	}

	/////////////////////////////////////////
	// Waila
	//

	@Override
	public final List<String> getWailaTooltip (final List<String> currentTooltip, final IDataAccessor accessor) {
		final String variant = accessor.getBlockState().getValue(VARIANT).getName();

		currentTooltip.add(Translation.localize("tooltip." + Soulus.MODID + ":summoner.style." + variant));

		return currentTooltip;
	}

}
