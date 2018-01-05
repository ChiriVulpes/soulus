package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.recipe.Recipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class OrbMurky extends SummonerUpgrade {

	public static final Serializer<OrbMurky> serializer = new Serializer<>(OrbMurky.class, "requiredEssence");

	public static final OrbMurky INSTANCE = new OrbMurky();

	public int requiredEssence = 128;

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
					} else if (stackItem == ModItems.ESSENCE || stackItem == ModItems.ASH) {
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
		if (tag != null && tag.hasKey("essence_quantity", 3)) {
			return tag.getInteger("essence_quantity");
		}
		return 0;
	}

	public static boolean isFilled(ItemStack stack) {
		return getContainedEssence(stack) >= INSTANCE.requiredEssence;
	}

	public static ItemStack setContainedEssence(ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setInteger("essence_quantity", count);
		return stack;
	}

	public static ItemStack setFilled(ItemStack stack) {
		return setContainedEssence(stack, INSTANCE.requiredEssence);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(this.getItemStack());
			items.add(this.getFilledStack());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int containedEssence = OrbMurky.getContainedEssence(stack);
		if (containedEssence < requiredEssence) {
			tooltip.add(I18n.format("tooltip." + Soulus.MODID + ":orb_murky.contained_essence", containedEssence,
					requiredEssence));
		}
	}
}