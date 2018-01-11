package yuudaari.soulus.common.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.recipe.ingredient.IngredientPotentialEssence;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.Soulus;

public class Soulbook extends ModItem {

	public final static Soulbook INSTANCE = new Soulbook();

	public static ItemStack getFilled (String essenceType) {
		return getStack(essenceType, Soulus.config.getSoulbookQuantity(essenceType));
	}

	public static ItemStack getStack (String essenceType) {
		return getStack(essenceType, 0);
	}

	public static ItemStack getStack (String essenceType, int essenceAmount) {
		ItemStack stack = new ItemStack(INSTANCE, 1);
		EssenceType.setEssenceType(stack, essenceType);
		setContainedEssence(stack, essenceAmount);
		return stack;
	}

	public static boolean isFilled (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return false;
		return getContainedEssence(stack) >= Soulus.config.getSoulbookQuantity(essenceType);
	}

	public static class SoulbookRecipe extends ShapelessOreRecipe {

		public static NonNullList<Ingredient> getIngredients (int size) {

			List<Ingredient> ingredients = new ArrayList<>();

			ingredients.addAll(Collections.nCopies(size * size - 1, IngredientPotentialEssence.INSTANCE));
			ingredients.add(Ingredient.fromItem(INSTANCE));

			return NonNullList.from(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		}

		public SoulbookRecipe (ResourceLocation name, int size) {
			super(new ResourceLocation(""), getIngredients(size), getFilled("unfocused"));
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
			ItemStack soulbook = null;
			String essenceType = null;
			int containedEssence = 0;
			int inventorySize = inv.getSizeInventory();
			for (int i = 0; i < inventorySize; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				Item stackItem = stack.getItem();
				if (stack == null || stackItem == Items.AIR)
					continue;
				if (stackItem == INSTANCE) {
					if (soulbook != null)
						return null;
					String itemTarget = EssenceType.getEssenceType(stack);
					if (itemTarget != null) {
						if (essenceType != null && !itemTarget.equals(essenceType))
							return null;
						essenceType = itemTarget;
					}
					containedEssence = getContainedEssence(stack);
					soulbook = stack;
					continue;
				} else if (stackItem == ModItems.ESSENCE) {
					String itemTarget = EssenceType.getEssenceType(stack);
					if (itemTarget == null || (essenceType != null && !itemTarget.equals(essenceType)))
						return null;
					essenceType = itemTarget;
					essenceCount++;
					continue;
				}
				return null;
			}
			if (soulbook != null && essenceCount > 0 && containedEssence + essenceCount <= Soulus.config
				.getSoulbookQuantity(essenceType)) {
				ItemStack newStack = soulbook.copy();
				EssenceType.setEssenceType(newStack, essenceType);
				setContainedEssence(newStack, containedEssence + essenceCount);
				return newStack;
			}
			return null;
		}
	}

	public Soulbook () {
		super("soulbook", 1);
		this.glint = true;
		setHasDescription();
	}

	@Override
	public void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
		registry.registerAll( //
			new SoulbookRecipe(getRegistryName(), 2), //
			new SoulbookRecipe(getRegistryName(), 3) //
		);
	}

	@Override
	public boolean hasEffect (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return false;
		int containedEssence = getContainedEssence(stack);
		return containedEssence >= Soulus.config.getSoulbookQuantity(essenceType);
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			essenceType = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + essenceType;
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		int containedEssence = getContainedEssence(stack);
		if (essenceType == null)
			return containedEssence == 0;
		return containedEssence < Soulus.config.getSoulbookQuantity(essenceType);
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return 1;
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) Soulus.config.getSoulbookQuantity(essenceType));
	}

	public static int getContainedEssence (ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("essence_quantity", 3)) {
			return tag.getInteger("essence_quantity");
		}
		return 0;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int containedEssence = Soulbook.getContainedEssence(stack);
		String mobTarget = EssenceType.getEssenceType(stack);
		if (mobTarget != null) {
			int requiredEssence = Soulus.config.getSoulbookQuantity(mobTarget);
			if (containedEssence < requiredEssence) {
				tooltip.add(I18n
					.format("tooltip." + Soulus.MODID + ":soulbook.contained_essence", containedEssence, requiredEssence));
			}
		}
	}
}
