package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.util.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BloodCrystal extends Item {
	private static int requiredBlood = 18;

	private static int colourEmpty = 0x281313;
	private static int colourFilled = 0xBC2044;

	public BloodCrystal () {
		super("bloodCrystal", 1);
		addFurnaceRecipe(new ItemStack(Blocks.SOUL_SAND));
	}

	public ItemStack getStack (int blood) {
		ItemStack stack = new ItemStack(this);
		setContainedBlood(stack, blood);
		return stack;
	}

	@Override
	public ItemStack getItemStack () {
		return getStack(0);
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		int containedBlood = BloodCrystal.getContainedBlood(stack);
		String name = super.getUnlocalizedNameInefficiently(stack);
		if (containedBlood == requiredBlood) {
			name = name.replace(
				":bloodCrystal", ":bloodCrystal.filled"
			);
		}
		return name;
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		return getContainedBlood(stack) < requiredBlood;
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		return (1 - getContainedBlood(stack) / (double) requiredBlood);
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick (ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			int containedBlood = getContainedBlood(itemStackIn);
			if (containedBlood < requiredBlood) {
				setContainedBlood(itemStackIn, containedBlood + 1);
				playerIn.attackEntityFrom(DamageSource.generic, 9);
				playerIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100));
				playerIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200));
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public void init () {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((ItemStack stack, int tintIndex) -> {
			float percentage = getContainedBlood(stack) / (float) requiredBlood;
			return Colour.mix(colourEmpty, colourFilled, percentage).get();
		}, this);
	}

	public static int getContainedBlood (ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("ContainedBlood", 1)) {
			return tag.getByte("ContainedBlood") - Byte.MIN_VALUE;
		}
		return 0;
	}

	public static ItemStack setContainedBlood (ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setByte("ContainedBlood", (byte) (count + Byte.MIN_VALUE));
		return stack;
	}
}
