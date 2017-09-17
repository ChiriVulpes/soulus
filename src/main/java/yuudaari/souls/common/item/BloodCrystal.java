package yuudaari.souls.common.item;

import yuudaari.souls.common.util.Colour;
import net.minecraft.entity.player.EntityPlayer;
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

public class BloodCrystal extends SummonerUpgrade {
	private static int requiredBlood = 18;

	private static int colourEmpty = 0x281313;
	private static int colourFilled = 0xBC2044;

	public BloodCrystal() {
		super("blood_crystal");

		registerColorHandler((ItemStack stack, int tintIndex) -> {
			float percentage = getContainedBlood(stack) / (float) requiredBlood;
			return Colour.mix(colourEmpty, colourFilled, percentage).get();
		});
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		// if it's full, allow them to be stacked
		return getContainedBlood(stack) == requiredBlood ? 16 : 1;
	}

	@Override
	public ItemStack getFilledStack() {
		return getStack(requiredBlood);
	}

	public ItemStack getStack(int blood) {
		ItemStack stack = new ItemStack(this);
		setContainedBlood(stack, blood);
		return stack;
	}

	@Override
	public ItemStack getItemStack() {
		return getStack(0);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		int containedBlood = getContainedBlood(stack);
		return containedBlood == requiredBlood;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		int containedBlood = getContainedBlood(stack);
		String name = super.getUnlocalizedNameInefficiently(stack);
		return containedBlood == requiredBlood ? name + ".filled" : name;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getContainedBlood(stack) < requiredBlood;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return (1 - getContainedBlood(stack) / (double) requiredBlood);
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (!worldIn.isRemote) {
			int containedBlood = getContainedBlood(heldItem);
			if (containedBlood < requiredBlood) {
				setContainedBlood(heldItem, containedBlood + 1);
				playerIn.attackEntityFrom(DamageSource.GENERIC, 9);
				playerIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100));
				playerIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200));
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, heldItem);
	}

	public static int getContainedBlood(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("ContainedBlood", 1)) {
			return tag.getByte("ContainedBlood") - Byte.MIN_VALUE;
		}
		return 0;
	}

	public static ItemStack setContainedBlood(ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setByte("ContainedBlood", (byte) (count + Byte.MIN_VALUE));
		return stack;
	}
}
