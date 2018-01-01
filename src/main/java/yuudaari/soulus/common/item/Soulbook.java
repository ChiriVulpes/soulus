package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.common.recipe.Recipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Soulbook extends ModItem {

	public Soulbook() {
		super("soulbook", 1);
		this.glint = true;

		Soulbook self = this;

		addRecipe(new Recipe(getRegistryName()) {

			@ParametersAreNonnullByDefault
			@Override
			public boolean matches(InventoryCrafting inv, World worldIn) {
				return getCraftingResult(inv) != null;
			}

			@ParametersAreNonnullByDefault
			@Nullable
			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
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
					if (stackItem == self) {
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
				if (soulbook != null && essenceCount > 0
						&& containedEssence + essenceCount <= Soulus.config.getSoulbookQuantity(essenceType)) {
					ItemStack newStack = soulbook.copy();
					EssenceType.setEssenceType(newStack, essenceType);
					setContainedEssence(newStack, containedEssence + essenceCount);
					return newStack;
				}
				return null;
			}

			@Nullable
			@Override
			public ItemStack getRecipeOutput() {
				return self.getItemStack(1);
			}
		});
	}

	public final static Soulbook INSTANCE = new Soulbook();

	public static ItemStack getFilled(String essenceType) {
		return getStack(essenceType, Soulus.config.getSoulbookQuantity(essenceType));
	}

	public static ItemStack getStack(String essenceType) {
		return getStack(essenceType, (byte) 0);
	}

	public static ItemStack getStack(String essenceType, byte essenceAmount) {
		ItemStack stack = new ItemStack(INSTANCE, 1);
		EssenceType.setEssenceType(stack, essenceType);
		setContainedEssence(stack, essenceAmount);
		return stack;
	}

	public static boolean isFilled(ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return false;
		return getContainedEssence(stack) >= Soulus.config.getSoulbookQuantity(essenceType);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return false;
		int containedEssence = getContainedEssence(stack);
		return containedEssence == Soulus.config.getSoulbookQuantity(essenceType);
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			essenceType = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + essenceType;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		int containedEssence = getContainedEssence(stack);
		if (essenceType == null)
			return containedEssence == 0;
		return containedEssence < Soulus.config.getSoulbookQuantity(essenceType);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return 1;
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) Soulus.config.getSoulbookQuantity(essenceType));
	}

	public static int getContainedEssence(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("essence_quantity", 1)) {
			return tag.getByte("essence_quantity") - Byte.MIN_VALUE;
		}
		return 0;
	}

	public static ItemStack setContainedEssence(ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setByte("essence_quantity", (byte) (count + Byte.MIN_VALUE));
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int containedEssence = Soulbook.getContainedEssence(stack);
		String mobTarget = EssenceType.getEssenceType(stack);
		if (mobTarget != null) {
			int requiredEssence = Soulus.config.getSoulbookQuantity(mobTarget);
			if (containedEssence < requiredEssence) {
				tooltip.add(I18n.format("tooltip." + Soulus.MODID + ":soulbook.contained_essence", containedEssence,
						requiredEssence));
			}
		}
	}
}