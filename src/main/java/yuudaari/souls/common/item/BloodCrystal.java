package yuudaari.souls.common.item;

import yuudaari.souls.common.config.PotionEffectSerializer;
import yuudaari.souls.common.config.Serializer;
import yuudaari.souls.common.misc.SoulsDamageSource;
import yuudaari.souls.common.util.Colour;
import yuudaari.souls.common.util.ModPotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

public class BloodCrystal extends SummonerUpgrade {
	private static int defaultRequiredBlood = 162;
	private static int defaultPrickAmount = 9;
	private static int defaultPrickWorth = 9;
	private static int defaultCreaturePrickRequiredHealth = 1;
	private static int defaultCreaturePrickAmount = 1;
	private static int defaultCreaturePrickWorth = 3;
	private static ModPotionEffect[] defaultPrickEffects = new ModPotionEffect[] { new ModPotionEffect("hunger", 100),
			new ModPotionEffect("nausea", 200) };

	private static int colourEmpty = 0x281313;
	private static int colourFilled = 0xBC2044;

	public int requiredBlood = defaultRequiredBlood;
	public int prickAmount = defaultPrickAmount;
	public int prickWorth = defaultPrickWorth;
	public int creaturePrickRequiredHealth = defaultCreaturePrickRequiredHealth;
	public int creaturePrickAmount = defaultCreaturePrickAmount;
	public int creaturePrickWorth = defaultCreaturePrickWorth;
	public ModPotionEffect[] prickEffects = defaultPrickEffects;

	public static Serializer<BloodCrystal> serializer;
	static {
		serializer = new Serializer<>(BloodCrystal.class, "requiredBlood", "prickAmount", "prickWorth",
				"creaturePrickRequiredHealth", "creaturePrickAmount", "creaturePrickWorth");

		serializer.fieldHandlers.put("prickEffects", PotionEffectSerializer.INSTANCE);
	}

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
		return getContainedBlood(stack) >= requiredBlood ? 16 : 1;
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
		return containedBlood >= requiredBlood;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		int containedBlood = getContainedBlood(stack);
		String name = super.getUnlocalizedNameInefficiently(stack);
		return containedBlood >= requiredBlood ? name + ".filled" : name;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getContainedBlood(stack) < requiredBlood;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - Math.min(requiredBlood, getContainedBlood(stack)) / (double) requiredBlood;
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (!worldIn.isRemote) {
			int containedBlood = getContainedBlood(heldItem);
			if (containedBlood < requiredBlood) {
				setContainedBlood(heldItem, containedBlood + prickAmount);
				playerIn.attackEntityFrom(SoulsDamageSource.BLOOD_CRYSTAL, prickAmount);

				for (ModPotionEffect effect : prickEffects)
					playerIn.addPotionEffect(effect);

				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, heldItem);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (target.getHealth() <= this.creaturePrickRequiredHealth / 2) {
			target.attackEntityFrom(SoulsDamageSource.BLOOD_CRYSTAL, this.creaturePrickAmount);
			int blood = getContainedBlood(stack);
			setContainedBlood(stack, blood + this.creaturePrickWorth);
		}
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot,
			ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 0, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) 0, 0));
		}

		return multimap;
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
