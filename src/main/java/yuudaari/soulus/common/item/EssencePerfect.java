package yuudaari.soulus.common.item;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.Sneak;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.recipe.ingredient.IngredientEssence;
import yuudaari.soulus.common.recipe.ingredient.IngredientEssence.AllowedStack;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.NBTHelper;
import yuudaari.soulus.common.util.NBTHelper.Tag;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class EssencePerfect extends Registration.Item {

	@Inject public static ConfigEssences CONFIG;

	public static class EssenceAlignment {

		private final NBTHelper alignment;
		private int total = -1;

		public EssenceAlignment () {
			alignment = new NBTHelper();
		}

		public EssenceAlignment (final ItemStack stack) {
			final NBTHelper stackNBT = new NBTHelper(stack);
			alignment = stackNBT.computeObject("essence_alignment", __ -> new NBTHelper());

			final String[] essenceTypes = stackNBT.getStringArray("essence_types");
			if (essenceTypes.length > 0) {
				for (final String essenceType : essenceTypes)
					add(essenceType, 1);
				stackNBT.remove("essence_types");
			}
		}

		private int getTotal () {
			if (total < 0)
				total = alignment.valueStream(Tag.INT)
					.collect(Collectors.summingInt(value -> ((NBTTagInt) value).getInt()));
			return total;
		}

		public double getAlignment (final String essenceType) {
			return alignment.getInteger(essenceType, 0) / (double) getTotal();
		}

		public int getRawAlignment (final String essenceType) {
			return alignment.getInteger(essenceType, 0);
		}

		public void add (final String essenceType, final int amount) {
			alignment.setInteger(essenceType, alignment.getInteger(essenceType, 0) + amount);
			if (total > -1)
				total += amount;
		}

		public void add (final EssenceAlignment alignments) {
			alignments.getEssenceTypes()
				.forEach(essenceType -> add(essenceType, alignments.getRawAlignment(essenceType)));
		}

		public Stream<String> getEssenceTypes () {
			return alignment.keyStream(Tag.INT);
		}

		public Stream<Map.Entry<String, Double>> getAlignments () {
			return getEssenceTypes()
				.map(essenceType -> new AbstractMap.SimpleEntry<>(essenceType, this.getAlignment(essenceType)));
		}

		public Stream<Map.Entry<String, Integer>> getRawAlignments () {
			return getEssenceTypes()
				.map(essenceType -> new AbstractMap.SimpleEntry<>(essenceType, this.getRawAlignment(essenceType)));
		}

		public void applyTo (final ItemStack stack) {
			new NBTHelper(stack)
				.setObject("essence_alignment", alignment);
		}
	}

	public static boolean isPerfect (final ItemStack stack) {
		final List<String> essenceTypesInStack = new EssenceAlignment(stack).getEssenceTypes()
			.sorted()
			.collect(Collectors.toList());
		final List<String> essenceTypes = CONFIG.getEssenceTypes()
			.sorted()
			.collect(Collectors.toList());
		return essenceTypes.equals(essenceTypesInStack);
	}


	public static class EssencePerfectRecipe extends ShapelessOreRecipe {

		private static NonNullList<Ingredient> ingredients () {
			final List<Ingredient> ingredients = new ArrayList<>();
			ingredients.addAll(Collections.nCopies(3 * 3, IngredientEssence.getInstance(AllowedStack.EMPTY, AllowedStack.ESSENCE_PERFECT)));
			return NonNullList.from(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		}

		public EssencePerfectRecipe (final ResourceLocation name) {
			super(new ResourceLocation(""), ingredients(), ItemRegistry.ESSENCE_PERFECT.getItemStack());
			setRegistryName(name);
		}

		@Override
		public boolean matches (final InventoryCrafting inv, final World worldIn) {
			return !getCraftingResult(inv).isEmpty();
		}

		@Override
		public ItemStack getCraftingResult (final InventoryCrafting inv) {

			final EssenceAlignment alignment = new EssenceAlignment();
			final int inventorySize = inv.getSizeInventory();
			int count = 0;

			for (int i = 0; i < inventorySize; i++) {
				final ItemStack stack = inv.getStackInSlot(i);
				final Item stackItem = stack.getItem();

				if (stack == null || stack.isEmpty())
					// empty slot, let's look at next
					continue;

				if (stackItem == ItemRegistry.ESSENCE) {
					final String essenceType = EssenceType.getEssenceType(stack);
					alignment.add(essenceType, 1);

				} else if (stackItem == ItemRegistry.ESSENCE_PERFECT)
					alignment.add(new EssenceAlignment(stack));

				else
					// some other random item
					return ItemStack.EMPTY;

				count++;
			}

			final String[] essenceTypes = alignment.getEssenceTypes().toArray(String[]::new);
			if (count < 2 || essenceTypes.length < 2)
				// existential essence requires at least 2 essence types
				return ItemStack.EMPTY;

			return ItemRegistry.ESSENCE_PERFECT.getItemStack(count, alignment);
		}
	}

	public EssencePerfect () {
		super("essence_perfect");
		setMaxStackSize(64);
		setHasDescription();

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (final ItemStack stack, final int tintIndex) -> {
				final String[] essenceTypes = new EssenceAlignment(stack).getEssenceTypes().toArray(String[]::new);
				if (essenceTypes.length == 0)
					return -1;

				// final String essenceType = essenceTypes[(int) (Math.random() * essenceTypes.length)];
				final String essenceType = essenceTypes[tintIndex % essenceTypes.length];
				return Essence.getColor(essenceType, tintIndex % 2);
			});
		}
	}

	public ItemStack getItemStack (final int count, final String... essenceTypes) {
		final ItemStack result = new ItemStack(ItemRegistry.ESSENCE_PERFECT);
		result.setCount(count);
		final EssenceAlignment alignment = new EssenceAlignment(result);
		for (final String essenceType : essenceTypes)
			alignment.add(essenceType, 1);
		return result;
	}

	public ItemStack getItemStack (final int count, final EssenceAlignment alignment) {
		final ItemStack result = new ItemStack(ItemRegistry.ESSENCE_PERFECT);
		result.setCount(count);
		alignment.applyTo(result);
		return result;
	}

	public ItemStack getPerfectStack () {
		return getPerfectStack(1);
	}

	public ItemStack getPerfectStack (final int count) {
		return getItemStack(count, CONFIG.getEssenceTypes().toArray(String[]::new));
	}

	@Override
	public void getSubItems (final CreativeTabs tab, final NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(getPerfectStack(1));
	}

	@Override
	public String getUnlocalizedNameInefficiently (final ItemStack stack) {
		String result = super.getUnlocalizedNameInefficiently(stack);
		return isPerfect(stack) ? result + ".complete" : result;
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return isPerfect(stack) ? EnumRarity.EPIC : EnumRarity.UNCOMMON;
	}

	// can't add glint due to a bug
	// @Override
	// public boolean hasEffect (final ItemStack stack) {
	// 	return isPerfect(stack);
	// }

	@Override
	public void onRegisterRecipes (final IForgeRegistry<IRecipe> registry) {
		registry.register(new EssencePerfectRecipe(getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation (final ItemStack stack, final World world, final List<String> tooltip, final ITooltipFlag flagIn) {
		final String registryName = getRegistryName().toString();
		final List<Map.Entry<String, Double>> essenceTypes = new EssenceAlignment(stack)
			.getAlignments()
			.sorted( (a, b) -> ((int) ((b.getValue() - a.getValue()) * 10000)) + a.getKey().compareTo(b.getKey()))
			.collect(Collectors.toList());

		final int totalEssenceTypes = essenceTypes.size();
		if (totalEssenceTypes < 3 || Sneak.isSneaking()) {
			final StringBuilder builder = new StringBuilder(Translation.localize("tooltip." + registryName + ".alignments"));
			for (int i = 0; i < totalEssenceTypes; i++) {
				final Map.Entry<String, Double> entry = essenceTypes.get(i);
				builder.append(EssenceType.localize(entry.getKey()));
				builder.append(Translation.localize("tooltip." + registryName + ".alignment_percentage", Translation.formatPercentage(entry.getValue())));
				if (i != totalEssenceTypes - 1)
					builder.append(Translation.localize("tooltip." + registryName + (i == totalEssenceTypes - 2 ? ".alignments_joining_last" : ".alignments_joining")));
			}

			tooltip.add(builder.toString());

		} else {
			tooltip.add(new Translation("tooltip." + registryName + ".alignments_count")
				.get(totalEssenceTypes, CONFIG.getEssenceTypes().count()));
			tooltip.add(Translation.localize("tooltip." + registryName + ".sneak_to_show_more"));
		}
	}
}
