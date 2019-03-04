package yuudaari.soulus.common.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.block.composer.ComposerCell.IHasComposerCellInfo;
import yuudaari.soulus.common.block.composer.IFillableWithEssence;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigSoulCatalyst;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Colour;
import yuudaari.soulus.common.util.Translation;

@ConfigInjected(Soulus.MODID)
public class SoulCatalyst extends Registration.Item implements IHasComposerCellInfo, IFillableWithEssence {

	@Inject public static ConfigSoulCatalyst CONFIG;

	public static class SoulCatalystFillRecipe extends ShapelessOreRecipe {

		public static NonNullList<Ingredient> getIngredients (int size) {

			List<Ingredient> ingredients = new ArrayList<>();

			ingredients.addAll(Collections.nCopies(size * size - 1, IngredientPotentialEssence.getInstance()));
			ingredients.add(Ingredient.fromItem(ItemRegistry.SOUL_CATALYST));

			return NonNullList.from(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		}

		public SoulCatalystFillRecipe (ResourceLocation name, int size) {
			super(new ResourceLocation(""), getIngredients(size), ItemRegistry.SOUL_CATALYST.getFilledStack());
			setRegistryName(name + "" + size);
		}

		@ParametersAreNonnullByDefault
		@Override
		public boolean matches (InventoryCrafting inv, World worldIn) {
			return getCraftingResult(inv) != null;
		}

		@ParametersAreNonnullByDefault
		@Nullable
		@Override
		public ItemStack getCraftingResult (InventoryCrafting inv) {
			int essenceCount = 0;
			ItemStack catalyst = null;
			int containedEssence = 0;
			int inventorySize = inv.getSizeInventory();
			for (int i = 0; i < inventorySize; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				Item stackItem = stack.getItem();
				if (stack == null || stackItem == Items.AIR)
					continue;
				if (stackItem == ItemRegistry.SOUL_CATALYST) {
					if (catalyst != null)
						return null;
					containedEssence = getContainedEssence(stack);
					catalyst = stack;
					continue;
				} else if (stackItem == ItemRegistry.ESSENCE || stackItem == ItemRegistry.ASH) {
					essenceCount++;
					continue;
				}
				return null;
			}
			if (catalyst != null && essenceCount > 0 && containedEssence + essenceCount <= CONFIG.requiredEssence) {
				ItemStack newStack = catalyst.copy();
				setContainedEssence(newStack, containedEssence + essenceCount);
				return newStack;
			}
			return null;
		}
	}

	private static final int colourEmpty = 0x222222;
	private static final int colourFilled = 0xFFFFFF;

	public SoulCatalyst () {
		super("soul_catalyst");
		setHasDescription();

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (ItemStack stack, int tintIndex) -> {
				float percentage = getContainedEssence(stack) / (float) CONFIG.requiredEssence;
				return Colour.mix(colourEmpty, colourFilled, percentage).get();
			});
		}
	}

	@Override
	public void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
		registry.registerAll( //
			new SoulCatalystFillRecipe(getRegistryName(), 2), //
			new SoulCatalystFillRecipe(getRegistryName(), 3) //
		);
	}

	@Override
	public int getItemStackLimit (ItemStack stack) {
		// if it's full, allow them to be stacked
		return getContainedEssence(stack) == CONFIG.requiredEssence ? CONFIG.stackSize : 1;
	}

	public ItemStack getFilledStack () {
		return getStack(CONFIG.requiredEssence);
	}

	public ItemStack getStack (int essence) {
		ItemStack stack = new ItemStack(this);
		setContainedEssence(stack, essence);
		return stack;
	}

	@Override
	public ItemStack getItemStack () {
		return getStack(0);
	}

	@Override
	public int fill (ItemStack currentStack, ItemStack fillWith, int quantity) {
		int currentEssence = getContainedEssence(currentStack);
		int insertQuantity = Math.max(0, Math.min(quantity, CONFIG.requiredEssence - currentEssence));

		if (insertQuantity > 0) setContainedEssence(currentStack, currentEssence + insertQuantity);

		return insertQuantity;
	}

	@Override
	public float getFillPercentage (ItemStack stack) {
		return getContainedEssence(stack) / (float) CONFIG.requiredEssence;
	}

	@Override
	public boolean hasEffect (ItemStack stack) {
		return getContainedEssence(stack) == CONFIG.requiredEssence;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		String result = super.getUnlocalizedNameInefficiently(stack);
		int containedEssence = getContainedEssence(stack);
		return containedEssence == CONFIG.requiredEssence ? result + ".filled" : result;
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return containedEssence < CONFIG.requiredEssence;
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) CONFIG.requiredEssence);
	}

	public static int getContainedEssence (ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("essence_quantity", 3)) {
			return tag.getInteger("essence_quantity");
		}
		return 0;
	}

	public static boolean isFilled (ItemStack stack) {
		return getContainedEssence(stack) >= CONFIG.requiredEssence;
	}

	public static ItemStack setContainedEssence (ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setInteger("essence_quantity", count);
		return stack;
	}

	public static ItemStack setFilled (ItemStack stack) {
		return setContainedEssence(stack, CONFIG.requiredEssence);
	}

	@Override
	public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(this.getItemStack());
			items.add(this.getFilledStack());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		addImportantInformation(tooltip, stack);
	}

	public void addImportantInformation (List<String> tooltip, ItemStack stack) {
		int containedEssence = OrbMurky.getContainedEssence(stack);
		if (containedEssence < CONFIG.requiredEssence) {
			tooltip.add(new Translation("tooltip." + Soulus.MODID + ":soul_catalyst.contained_essence")
				.addArgs(containedEssence, CONFIG.requiredEssence)
				.get());
		}
	}

	@Override
	public void addComposerCellInfo (List<String> tooltip, ItemStack stack, int stackSize) {
		if (stackSize == 1) addImportantInformation(tooltip, stack);
	}

}

