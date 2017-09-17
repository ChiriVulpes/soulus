package yuudaari.souls.common.item;

import yuudaari.souls.common.ModItems;
import yuudaari.souls.common.recipe.Recipe;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class OrbMurky extends SummonerUpgrade {

	private int requiredEssence = 64;

	public OrbMurky() {
		super("orb_murky");

		OrbMurky self = this;

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
				ItemStack orb = null;
				int containedEssence = 0;
				int inventorySize = inv.getSizeInventory();
				for (int i = 0; i < inventorySize; i++) {
					ItemStack stack = inv.getStackInSlot(i);
					Item stackItem = stack.getItem();
					if (stack == null || stackItem == Items.AIR)
						continue;
					if (stackItem == self) {
						if (orb != null)
							return null;
						containedEssence = getContainedEssence(stack);
						orb = stack;
						continue;
					} else if (stackItem == ModItems.ESSENCE) {
						essenceCount++;
						continue;
					}
					return null;
				}
				if (orb != null && essenceCount > 0 && containedEssence + essenceCount <= requiredEssence) {
					ItemStack newStack = orb.copy();
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

	@Override
	public int getItemStackLimit(ItemStack stack) {
		// if it's full, allow them to be stacked
		return getContainedEssence(stack) == requiredEssence ? 16 : 1;
	}

	@Override
	public ItemStack getFilledStack() {
		return getStack(requiredEssence);
	}

	public ItemStack getStack(int essence) {
		ItemStack stack = new ItemStack(this);
		setContainedEssence(stack, essence);
		return stack;
	}

	@Override
	public ItemStack getItemStack() {
		return getStack(0);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return getContainedEssence(stack) == requiredEssence;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String result = super.getUnlocalizedNameInefficiently(stack);
		int containedEssence = getContainedEssence(stack);
		return containedEssence == requiredEssence ? result + ".filled" : result;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return containedEssence < requiredEssence;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) requiredEssence);
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
}