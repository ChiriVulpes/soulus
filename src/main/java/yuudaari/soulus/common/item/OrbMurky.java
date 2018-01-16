package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigOrbMurky;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ConfigInjected(Soulus.MODID)
public class OrbMurky extends SummonerUpgrade {

	@Inject(ConfigOrbMurky.class) public static ConfigOrbMurky CONFIG;

	public static class OrbMurkyFillRecipe extends ShapelessOreRecipe {

		public static NonNullList<Ingredient> getIngredients (int size) {

			List<Ingredient> ingredients = new ArrayList<>();

			ingredients.addAll(Collections.nCopies(size * size - 1, IngredientPotentialEssence.getInstance()));
			ingredients.add(Ingredient.fromItem(ModItems.ORB_MURKY));

			return NonNullList.from(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		}

		public OrbMurkyFillRecipe (ResourceLocation name, int size) {
			super(new ResourceLocation(""), getIngredients(size), ModItems.ORB_MURKY.getFilledStack());
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
			ItemStack orb = null;
			int containedEssence = 0;
			int inventorySize = inv.getSizeInventory();
			for (int i = 0; i < inventorySize; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				Item stackItem = stack.getItem();
				if (stack == null || stackItem == Items.AIR)
					continue;
				if (stackItem == ModItems.ORB_MURKY) {
					if (orb != null)
						return null;
					containedEssence = getContainedEssence(stack);
					orb = stack;
					continue;
				} else if (stackItem == ModItems.ESSENCE || stackItem == ModItems.ASH) {
					essenceCount++;
					continue;
				}
				return null;
			}
			if (orb != null && essenceCount > 0 && containedEssence + essenceCount <= CONFIG.requiredEssence) {
				ItemStack newStack = orb.copy();
				setContainedEssence(newStack, containedEssence + essenceCount);
				return newStack;
			}
			return null;
		}
	}

	public OrbMurky () {
		super("orb_murky");
		setHasDescription();
	}

	@Override
	public void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
		registry.registerAll( //
			new OrbMurkyFillRecipe(getRegistryName(), 2), //
			new OrbMurkyFillRecipe(getRegistryName(), 3) //
		);
	}

	@Override
	public int getItemStackLimit (ItemStack stack) {
		// if it's full, allow them to be stacked
		return getContainedEssence(stack) == CONFIG.requiredEssence ? 16 : 1;
	}

	@Override
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int containedEssence = OrbMurky.getContainedEssence(stack);
		if (containedEssence < CONFIG.requiredEssence) {
			tooltip.add(I18n
				.format("tooltip." + Soulus.MODID + ":orb_murky.contained_essence", containedEssence, CONFIG.requiredEssence));
		}
	}
}
