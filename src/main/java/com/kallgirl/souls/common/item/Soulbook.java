package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.SpawnMap;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Soulbook extends Item {
	private static String getMobTarget(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("EntityTag", 10)) {
			tag = tag.getCompoundTag("EntityTag");
			if (tag.hasKey("id", 8)) {
				return tag.getString("id");
			}
		}
		return null;
	}
	private static ItemStack setMobTarget(ItemStack stack, String mobTarget) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		if (tag.hasKey("EntityTag", 10)) {
			tag = tag.getCompoundTag("EntityTag");
		} else {
			NBTTagCompound entityTag = new NBTTagCompound();
			tag.setTag("EntityTag", entityTag);
			tag = entityTag;
		}
		tag.setString("id", mobTarget);
		return stack;
	}
	private static int getContainedEssence(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("ContainedEssence", 1)) {
			return tag.getByte("ContainedEssence") - Byte.MIN_VALUE;
		}
		return 0;
	}
	private static ItemStack setContainedEssence(ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setByte("ContainedEssence", (byte)(count + Byte.MIN_VALUE));
		System.out.println("cast: " + (byte)(count + Byte.MIN_VALUE));
		return stack;
	}
	public Soulbook() {
		super("soulbook", 1);
		glint = true;
		addRecipeShapeless("dustEnder", Items.BOOK);
		Item self = this;
		GameRegistry.addRecipe(new IRecipe() {
			@ParametersAreNonnullByDefault
			@Override
			public boolean matches (InventoryCrafting inv, World worldIn) {
				int essenceCount = 0;
				boolean hasSoulbook = false;
				String target = null;
				int containedEssence = Byte.MIN_VALUE;
				int inventorySize = inv.getSizeInventory();
				for (int i = 0; i < inventorySize; i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack == null) continue;
					if (stack.getItem() == self) {
						if (hasSoulbook) return false;
						String itemTarget = Soulbook.getMobTarget(stack);
						if (itemTarget != null) {
							if (target != null && !itemTarget.equals(target))
								return false;
							target = itemTarget;
						}
						containedEssence = Soulbook.getContainedEssence(stack);
						hasSoulbook = true;
						continue;
					} else if (stack.getItem() == ModObjects.getItem("essence")) {
						String itemTarget = Soulbook.getMobTarget(stack);
						if (itemTarget == null || (target != null && !itemTarget.equals(target)))
							return false;
						target = itemTarget;
						essenceCount++;
						continue;
					}
					return false;
				}
				System.out.println("Target: " + target + ", hasSoulbook: " + hasSoulbook + ", essence: " + essenceCount);
				return hasSoulbook && essenceCount > 0 && containedEssence + essenceCount <= SpawnMap.map.get(target).required;
			}

			@ParametersAreNonnullByDefault
			@Nullable
			@Override
			public ItemStack getCraftingResult (InventoryCrafting inv) {
				int essenceCount = 0;
				ItemStack soulbook = null;
				String target = null;
				int containedEssence = 0;
				int inventorySize = inv.getSizeInventory();
				for (int i = 0; i < inventorySize; i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack == null) continue;
					if (stack.getItem() == self) {
						if (soulbook != null) return null;
						String itemTarget = Soulbook.getMobTarget(stack);
						if (itemTarget != null) {
							if (target != null && !itemTarget.equals(target))
								return null;
							target = itemTarget;
						}
						containedEssence = Soulbook.getContainedEssence(stack);
						soulbook = stack;
						continue;
					} else if (stack.getItem() == ModObjects.getItem("essence")) {
						String itemTarget = Soulbook.getMobTarget(stack);
						if (itemTarget == null || (target != null && !itemTarget.equals(target)))
							return null;
						target = itemTarget;
						essenceCount++;
						continue;
					}
					return null;
				}
				System.out.println("Target: " + target + ", hasSoulbook: " + soulbook + ", essence: " + essenceCount);
				if (soulbook != null && essenceCount > 0 && containedEssence + essenceCount <= SpawnMap.map.get(target).required) {
					ItemStack newStack = soulbook.copy();
					Soulbook.setMobTarget(newStack, target);
					Soulbook.setContainedEssence(newStack, containedEssence + essenceCount);
					System.out.println("new essencecount: " + Soulbook.getContainedEssence(newStack) + ", target: " + Soulbook.getMobTarget(newStack));
					return newStack;
				}
				System.out.println("null");
				return null;
			}

			@Override
			public int getRecipeSize () {
				return 4;
			}

			@Nullable
			@Override
			public ItemStack getRecipeOutput () {
				return self.getItemStack(1);
			}

			@ParametersAreNonnullByDefault
			@Nonnull
			@Override
			public ItemStack[] getRemainingItems (InventoryCrafting inv) {
				return ForgeHooks.defaultRecipeGetRemainingItems(inv);
			}
		});
	}
	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}
	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		Soulbook.setMobTarget(stack, mobTarget);
		Soulbook.setContainedEssence(stack, 0);
		return stack;
	}

	@Override
	public boolean hasEffect (ItemStack stack) {
		return Soulbook.getMobTarget(stack) != null;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		String mobTarget = Soulbook.getMobTarget(stack);
		if (mobTarget == null) mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack).replace(
			":soulbook", ":soulbook." + mobTarget
		);
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		String mobTarget = Soulbook.getMobTarget(stack);
		if (mobTarget == null) return true;
		int containedEssence = Soulbook.getContainedEssence(stack);
		SpawnMap.SpawnInfo target = SpawnMap.map.get(mobTarget);
		return containedEssence < target.required;
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		String mobTarget = Soulbook.getMobTarget(stack);
		if (mobTarget == null) return 1;
		int containedEssence = Soulbook.getContainedEssence(stack);
		SpawnMap.SpawnInfo target = SpawnMap.map.get(mobTarget);
		return (1 - containedEssence / (double)target.required);
	}
}