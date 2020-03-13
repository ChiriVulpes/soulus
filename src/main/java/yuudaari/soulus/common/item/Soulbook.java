package yuudaari.soulus.common.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import scala.Tuple2;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity.IHasComposerCellInfo;
import yuudaari.soulus.common.block.composer.IFillableWithEssence;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigColor;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.recipe.ingredient.IngredientNBTSensitive;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Colour;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.XP;

@ConfigInjected(Soulus.MODID)
public class Soulbook extends Registration.Item implements IHasComposerCellInfo, IFillableWithEssence {

	@Inject public static ConfigEssences CONFIG;

	public static ItemStack getEmpty () {
		return getStack(null);
	}

	public static ItemStack getFilled (String essenceType) {
		return getStack(essenceType, CONFIG.getSoulbookQuantity(essenceType));
	}

	public static ItemStack getStack (String essenceType) {
		return getStack(essenceType, 0);
	}

	public static ItemStack getStack (String essenceType, int essenceAmount) {
		ItemStack stack = new ItemStack(ItemRegistry.SOULBOOK, 1);
		if (essenceAmount > 0) {
			EssenceType.setEssenceType(stack, essenceType);
			setContainedEssence(stack, essenceAmount);
		}
		return stack;
	}

	public static boolean isFilled (ItemStack stack) {
		if (stack.getItem() != ItemRegistry.SOULBOOK)
			return false;

		final String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return false;

		return getContainedEssence(stack) >= CONFIG.getSoulbookQuantity(essenceType);
	}

	public static class SoulbookRecipe extends ShapelessOreRecipe {

		public static NonNullList<Ingredient> getIngredients (final String essenceType, final int size) {

			List<Ingredient> ingredients = new ArrayList<>();

			ingredients.addAll(Collections.nCopies(size * size - 1, new IngredientNBTSensitive(Essence.getStack(essenceType))));
			ingredients.add(new IngredientNBTSensitive(ItemRegistry.SOULBOOK.getItemStack()));

			return NonNullList.from(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		}

		public final String essenceType;

		public SoulbookRecipe (final String registryName, final String essenceType, final int size) {
			super(new ResourceLocation(""), getIngredients(essenceType, size), getFilled(essenceType));
			setRegistryName(registryName + "_" + essenceType.replace(":", "_") + "_" + size);
			this.essenceType = essenceType;
		}

		@ParametersAreNonnullByDefault
		@Override
		public boolean matches (InventoryCrafting inv, World worldIn) {
			return !getCraftingResult(inv).isEmpty();
		}

		@ParametersAreNonnullByDefault
		@Override
		public ItemStack getCraftingResult (final InventoryCrafting inv) {
			return process(inv)._2();
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems (final InventoryCrafting inv) {
			return process(inv)._1();
		}

		public Tuple2<NonNullList<ItemStack>, ItemStack> process (final InventoryCrafting inv) {
			final NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
			int essenceCount = 0;
			// int containedEssence = 0;

			final int inventorySize = inv.getSizeInventory();
			for (int i = 0; i < inventorySize; i++) {

				final ItemStack stack = inv.getStackInSlot(i);
				final Item stackItem = stack.getItem();

				if (stack == null || stack.isEmpty())
					continue;

				if (stackItem == ItemRegistry.SOULBOOK) {
					if (!essenceType.equals(EssenceType.getEssenceType(stack)))
						return new Tuple2<>(ret, ItemStack.EMPTY);

					ret.set(i, Soulbook.getEmpty());
					essenceCount += getContainedEssence(stack);
					// containedEssence = getContainedEssence(stack);
					// soulbook = stack;
					continue;

				} else if (stackItem == ItemRegistry.ESSENCE) {
					if (!essenceType.equals(EssenceType.getEssenceType(stack)))
						return new Tuple2<>(ret, ItemStack.EMPTY);

					essenceCount++;
					continue;
				}

				// some other random item, we didn't match
				return new Tuple2<>(ret, ItemStack.EMPTY);
			}

			if (essenceType != null) {
				final int maxEssence = CONFIG.getSoulbookQuantity(essenceType);
				ItemStack soulbook = null;

				for (int i = 0; i < inventorySize && essenceCount > 0; i++) {
					final ItemStack itemInSlotToReturn = ret.get(i);
					if (itemInSlotToReturn.getItem() == ItemRegistry.SOULBOOK) {
						if (soulbook == null) {
							soulbook = itemInSlotToReturn;
							ret.set(i, ItemStack.EMPTY);
						}

						if (essenceCount >= maxEssence) {
							setContainedEssence(itemInSlotToReturn, maxEssence);
							EssenceType.setEssenceType(itemInSlotToReturn, essenceType);
							essenceCount -= maxEssence;

						} else if (essenceCount > 0) {
							setContainedEssence(itemInSlotToReturn, essenceCount);
							EssenceType.setEssenceType(itemInSlotToReturn, essenceType);
							essenceCount = 0;
						}
					}
				}

				if (soulbook != null && getContainedEssence(soulbook) > 0)
					return new Tuple2<>(ret, soulbook);
			}

			return new Tuple2<>(ret, ItemStack.EMPTY);
		}
	}

	public Soulbook () {
		super("soulbook");
		setMaxStackSize(1);
		setHasDescription();

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler( (ItemStack stack, int tintIndex) -> {
				if (tintIndex == 2) return -1;

				int defaultColour = tintIndex == 0 ? 0x333F58 : 0x5E5997;

				String essenceType = EssenceType.getEssenceType(stack);
				if (essenceType == null)
					return defaultColour;

				EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(essenceType));
				if (entry == null)
					return defaultColour;

				ConfigEssence essenceConfig = CONFIG.get(essenceType);
				if (essenceConfig == null)
					return defaultColour;

				ConfigColor colors = essenceConfig.colors;
				if (colors == null) {
					EntityList.EntityEggInfo eggInfo = entry.getEgg();
					if (eggInfo == null)
						return defaultColour;
					colors = new ConfigColor(eggInfo);
				}

				int color = tintIndex == 0 ? colors.primary : colors.secondary;
				if (Summoner.CONFIG.soulbookEssenceRequiredToInsert <= 0) return color;

				double percent = getContainedEssence(stack) / Summoner.CONFIG.soulbookEssenceRequiredToInsert / (double) essenceConfig.soulbookQuantity;
				return Colour.mix(defaultColour, color, percent).get();
			});
		}
	}

	@Override
	public EnumRarity getRarity (final ItemStack stack) {
		return isFilled(stack) ? EnumRarity.UNCOMMON : super.getRarity(stack);
	}

	@Override
	public void onCreated (final ItemStack stack, final World world, final EntityPlayer player) {
		if (player == null || !isFilled(stack))
			return;

		final String essenceType = EssenceType.getEssenceType(stack);

		for (int i = 0; i < stack.getCount(); i++)
			XP.grant(player, CONFIG.getSoulbookXP(essenceType).getInt(world.rand));
	}

	@Override
	public void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
		registry.registerAll(CONFIG.getEssenceTypes()
			.flatMap(essenceType -> IntStream.range(2, 4)
				.boxed()
				.map(size -> new SoulbookRecipe(getRegistryName().toString(), essenceType, size)))
			.toArray(SoulbookRecipe[]::new));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		ConfigEssence config = CONFIG.get(essenceType);
		if (essenceType == null || config == null)
			return Translation.localize(this.getUnlocalizedName() + ".unfocused.name").trim();

		String alignment = config.name;
		if (alignment == null)
			alignment = EssenceType.localize(essenceType);

		return Translation.localize(this.getUnlocalizedName() + ".focused.name", alignment).trim();
	}

	@Override
	public boolean showDurabilityBar (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		int containedEssence = getContainedEssence(stack);
		if (essenceType == null)
			return containedEssence == 0;
		return containedEssence < CONFIG.getSoulbookQuantity(essenceType);
	}

	@Override
	public double getDurabilityForDisplay (ItemStack stack) {
		String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return 1;
		int containedEssence = getContainedEssence(stack);
		return (1 - containedEssence / (double) CONFIG.getSoulbookQuantity(essenceType));
	}

	@Override
	public int fillWithEssence (final ItemStack currentStack, final ItemStack fillWith, final int quantity) {
		final int currentEssence = getContainedEssence(currentStack);
		String essenceType = EssenceType.getEssenceType(currentStack);
		final String fillWithEssenceType = EssenceType.getEssenceType(fillWith);

		if (fillWithEssenceType == null || //
			(essenceType != null && !essenceType.equals(fillWithEssenceType)))
			return 0;

		if (essenceType == null)
			EssenceType.setEssenceType(currentStack, essenceType = fillWithEssenceType);

		final int requiredEssence = CONFIG.getSoulbookQuantity(essenceType);
		final int insertQuantity = Math.max(0, Math.min(quantity, requiredEssence - currentEssence));

		if (insertQuantity > 0)
			setContainedEssence(currentStack, currentEssence + insertQuantity);

		return insertQuantity;
	}

	@Override
	public float getEssenceFillPercentage (final ItemStack stack) {
		final String essenceType = EssenceType.getEssenceType(stack);
		if (essenceType == null)
			return 0;

		final int requiredEssence = CONFIG.getSoulbookQuantity(essenceType);
		if (requiredEssence < 0)
			return 0;

		return getContainedEssence(stack) / (float) requiredEssence;
	}

	@Override
	public boolean isFilledWithEssence (final ItemStack stack) {
		return isFilled(stack);
	}

	public static int getContainedEssence (ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("essence_quantity", 3)) {
			return tag.getInteger("essence_quantity");
		}
		return 0;
	}

	public static ItemStack setContainedEssence (ItemStack stack, int count) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setInteger("essence_quantity", count);
		return stack;
	}

	@Override
	public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		items.add(getItemStack());
		CONFIG.getEssenceTypes()
			.map(Soulbook::getFilled)
			.forEach(items::add);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		addImportantInformation(tooltip, stack);
	}

	public void addImportantInformation (List<String> tooltip, ItemStack stack) {
		int containedEssence = Soulbook.getContainedEssence(stack);
		String mobTarget = EssenceType.getEssenceType(stack);
		if (mobTarget != null) {
			int requiredEssence = CONFIG.getSoulbookQuantity(mobTarget);
			if (containedEssence < requiredEssence) {
				tooltip.add(new Translation("tooltip." + Soulus.MODID + ":soulbook.contained_essence")
					.addArgs(containedEssence, requiredEssence)
					.get());
			}
		}
	}

	@Override
	public void addComposerCellInfo (List<String> tooltip, ItemStack stack, int stackSize) {
		if (stackSize == 1) addImportantInformation(tooltip, stack);
	}
}
