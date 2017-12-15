package yuudaari.souls.common.item;

import yuudaari.souls.Souls;
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
				String target = null;
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
						String itemTarget = MobTarget.getMobTarget(stack);
						if (itemTarget != null) {
							if (target != null && !itemTarget.equals(target))
								return null;
							target = itemTarget;
						}
						containedEssence = getContainedEssence(stack);
						soulbook = stack;
						continue;
					} else if (stackItem == ModItems.ESSENCE) {
						String itemTarget = MobTarget.getMobTarget(stack);
						if (itemTarget == null || (target != null && !itemTarget.equals(target)))
							return null;
						target = itemTarget;
						essenceCount++;
						continue;
					}
					return null;
				}
				if (soulbook != null && essenceCount > 0
						&& containedEssence + essenceCount <= Souls.getSoulInfo(target).quantity) {
					ItemStack newStack = soulbook.copy();
					MobTarget.setMobTarget(newStack, target);
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
		setContainedEssence(stack, 0);
		return stack;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		if (mobTarget == null)
			return false;
		int containedEssence = getContainedEssence(stack);
		return containedEssence == Souls.getSoulInfo(mobTarget).quantity;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		if (mobTarget == null)
			mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + mobTarget;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		int containedEssence = getContainedEssence(stack);
		if (mobTarget == null)
			return containedEssence == 0;
		return containedEssence < Souls.getSoulInfo(mobTarget).quantity;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		if (mobTarget == null)
			return 1;
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) Souls.getSoulInfo(mobTarget).quantity);
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