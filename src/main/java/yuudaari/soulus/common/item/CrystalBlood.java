package yuudaari.soulus.common.item;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigCrystalBlood;
import yuudaari.soulus.common.misc.ModDamageSource;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.CrystalBloodHitEntity;
import yuudaari.soulus.common.util.Colour;
import yuudaari.soulus.common.util.LangHelper;
import yuudaari.soulus.common.util.ModPotionEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ConfigInjected(Soulus.MODID)
public class CrystalBlood extends SummonerUpgrade {

	@Inject public static ConfigCrystalBlood CONFIG;

	private static final int colourEmpty = 0x281313;
	private static final int colourFilled = 0xBC2044;

	public CrystalBlood () {
		super("crystal_blood");

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (ItemStack stack, int tintIndex) -> {
				float percentage = getContainedBlood(stack) / (float) CONFIG.requiredBlood;
				return Colour.mix(colourEmpty, colourFilled, percentage).get();
			});
		}

		setHasDescription();
	}

	@Override
	public int getItemStackLimit (ItemStack stack) {
		// if it's full, allow them to be stacked
		return getContainedBlood(stack) >= CONFIG.requiredBlood ? 16 : 1;
	}

	@Override
	public ItemStack getFilledStack () {
		return getStack(CONFIG.requiredBlood);
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

	@Override
	public boolean hasEffect (ItemStack stack) {
		int containedBlood = getContainedBlood(stack);
		return containedBlood >= CONFIG.requiredBlood;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		int containedBlood = getContainedBlood(stack);
		String name = super.getUnlocalizedNameInefficiently(stack);
		return containedBlood >= CONFIG.requiredBlood ? name + ".filled" : name;
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		return getContainedBlood(stack) < CONFIG.requiredBlood;
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		return 1 - Math.min(CONFIG.requiredBlood, getContainedBlood(stack)) / (double) CONFIG.requiredBlood;
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer player, EnumHand hand) {

		ItemStack heldItem = player.getHeldItem(hand);
		int containedBlood = getContainedBlood(heldItem);
		if (containedBlood < CONFIG.requiredBlood) {

			if (player instanceof FakePlayer) {
				heldItem.setCount(0);
				EntityItem dropEntity = new EntityItem(player.world, player.posX, player.posY, player.posZ, ModItems.CRYSTAL_BLOOD_BROKEN
					.getItemStack());
				dropEntity.setNoPickupDelay();
				player.world.spawnEntity(dropEntity);

			} else {
				if (!worldIn.isRemote) {
					setContainedBlood(heldItem, containedBlood + CONFIG.prickWorth);
					player.attackEntityFrom(ModDamageSource.CRYSTAL_BLOOD, CONFIG.prickAmount);

					for (ModPotionEffect effect : CONFIG.prickEffects)
						effect.apply(player);

					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
				} else {
					if (player.world.isRemote) {
						particles(player);
					}
				}
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.FAIL, heldItem);
	}

	@Override
	public boolean hitEntity (ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (target.getHealth() <= CONFIG.creaturePrickRequiredHealth) {
			target.attackEntityFrom(ModDamageSource.CRYSTAL_BLOOD, CONFIG.creaturePrickAmount);
			int blood = getContainedBlood(stack);
			setContainedBlood(stack, blood + CONFIG.creaturePrickWorth);
			CrystalBlood.bloodParticles(target);
		}
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers (EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			int containedBlood = getContainedBlood(stack);
			if (containedBlood < CONFIG.requiredBlood) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE
					.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 0, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED
					.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) 0, 0));
			}
		}

		return multimap;
	}

	public static int getContainedBlood (ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("contained_blood", 3)) {
			return tag.getInteger("contained_blood");
		}
		return 0;
	}

	public static boolean isFilled (ItemStack stack) {
		return getContainedBlood(stack) >= CONFIG.requiredBlood;
	}

	public static ItemStack setContainedBlood (ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setInteger("contained_blood", count);
		return stack;
	}

	public static ItemStack setFilled (ItemStack stack) {
		return setContainedBlood(stack, CONFIG.requiredBlood);
	}

	public static void bloodParticles (EntityLivingBase entity) {
		if (entity.world.isRemote) {
			particles(entity);
		} else {
			SoulsPacketHandler.INSTANCE
				.sendToAllAround(new CrystalBloodHitEntity(entity), new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 128));
		}
	}

	@SideOnly(Side.CLIENT)
	private static void particles (EntityLivingBase entity) {
		World world = entity.getEntityWorld();
		Random rand = world.rand;

		for (int i = 0; i < CONFIG.particleCount; ++i) {
			double d3 = (entity.posX - 0.5F + rand.nextFloat());
			double d4 = (entity.posY + rand.nextFloat());
			double d5 = (entity.posZ - 0.5F + rand.nextFloat());
			double d3o = (d3 - entity.posX) / 5;
			double d4o = (d4 - entity.posY) / 5;
			double d5o = (d5 - entity.posZ) / 5;
			world.spawnParticle(ParticleType.BLOOD.getId(), false, d3, d4, d5, d3o, d4o, d5o, 1);
		}
	}

	@Override
	public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(this.getItemStack());
			items.add(this.getFilledStack());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int containedBlood = CrystalBlood.getContainedBlood(stack);
		if (containedBlood < CONFIG.requiredBlood) {
			tooltip.add(LangHelper.localize("tooltip." + Soulus.MODID + ":crystal_blood.contained_blood", containedBlood, CONFIG.requiredBlood));
		}
	}
}
