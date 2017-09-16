package yuudaari.souls.common.item;

import yuudaari.souls.common.ModItems;
import yuudaari.souls.common.util.MobTarget;
import yuudaari.souls.common.util.ModItem;
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

public class OrbMurky extends ModItem {

	private int neededEssence = 64;

	public OrbMurky() {
		super("orb_murky", 1);

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
				if (orb != null && essenceCount > 0 && containedEssence + essenceCount <= neededEssence) {
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

	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}

	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		MobTarget.setMobTarget(stack, mobTarget);
		Soulbook.setContainedEssence(stack, 0);
		return stack;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return getContainedEssence(stack) == neededEssence;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String result = super.getUnlocalizedNameInefficiently(stack);
		int containedEssence = getContainedEssence(stack);
		return containedEssence == neededEssence ? result + ".filled" : result;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return containedEssence < neededEssence;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) neededEssence);
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