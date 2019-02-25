package yuudaari.soulus.common.item;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.util.IModThing;
import yuudaari.soulus.common.util.Translation;

public class Barket extends UniversalBucket implements IModThing {


	public static class BarketRepairRecipe extends RecipeRepairItem {

		@Override
		public boolean matches (final InventoryCrafting inv, final World worldIn) {
			int count = 0;

			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				final ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;

				if (stack.getItem() != ModItems.BARKET || stack.getCount() != 1 || ++count > 2) return false;
			}

			return true;
		}

		@Override
		public ItemStack getCraftingResult (final InventoryCrafting inv) {
			ModItems.BARKET.repairable = true;
			final ItemStack result = super.getCraftingResult(inv);
			ModItems.BARKET.repairable = false;
			return result;
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems (final InventoryCrafting inv) {
			return NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		}
	}

	private final ItemStack EMPTY = new ItemStack(this);

	public int maxDamage = 100;
	public boolean repairable = false;

	public Barket () {
		super(1000, ItemStack.EMPTY, true);
		setCreativeTab(CreativeTab.INSTANCE);
		setRegistryName(Soulus.MODID + ":" + getName());
		setUnlocalizedName(getRegistryName().toString());
		setMaxDamage(maxDamage);
	}

	@Override
	public void onRegisterRecipes (final IForgeRegistry<IRecipe> registry) {
		registry.register(new BarketRepairRecipe()
			.setRegistryName(Soulus.MODID + ":" + getName() + "_repair"));
	}

	@Override
	public boolean isRepairable () {
		// we provide the repair recipe ourselves ... by default, this is false, so the normal repair recipe doesn't work
		// however, when the BarketRepairRecipe tries to get the crafting result, so we can use the old code, we set "repairable"
		// to true temporarily
		return repairable;
	}

	@Override
	public int getItemBurnTime (final ItemStack itemStack) {
		return getFluid(itemStack) == null ? 120 : 0;
	}

	@Override
	public String getName () {
		return "barket";
	}

	@Override
	public void onUpdate (final ItemStack stack, final World worldIn, final Entity entityIn, final int itemSlot, final boolean isSelected) {
		final FluidStack fluid = getFluid(stack);
		if (fluid == null) return;

		stack.damageItem(1, (EntityLivingBase) entityIn);
		if (stack.getCount() == 0 && entityIn instanceof EntityPlayer && !worldIn.isRemote) {
			stack.setCount(1);
			FluidUtil.tryPlaceFluid((EntityPlayer) entityIn, worldIn, entityIn.getPosition(), stack, fluid);
			stack.setCount(0);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation (final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
		return slotChanged || !fluidsEqual(oldStack, newStack);
	}

	@Override
	public ItemStack getContainerItem (final ItemStack itemStack) {
		final int durability = itemStack.getItemDamage();
		final ItemStack result = super.getContainerItem(itemStack);
		result.setItemDamage(durability);
		return result;
	}

	public boolean fluidsEqual (final ItemStack s1, final ItemStack s2) {
		final FluidStack f1 = getFluid(s1);
		final FluidStack f2 = getFluid(s2);
		return f1 == null ? f2 == null : f1.equals(f2);
	}

	@Override
	public void getSubItems (final @Nullable CreativeTabs tab, final NonNullList<ItemStack> subItems) {
		if (!this.isInCreativeTab(tab))
			return;

		subItems.add(EMPTY);

		final FluidStack fs = new FluidStack(FluidRegistry.WATER, getCapacity());
		final ItemStack stack = new ItemStack(this);
		final IFluidHandlerItem fluidHandler = stack
			.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		if (fluidHandler != null && fluidHandler.fill(fs, true) == fs.amount) {
			final ItemStack filled = fluidHandler.getContainer();
			subItems.add(filled);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick (final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack heldItem = player.getHeldItem(hand);
		final FluidStack fluidStack = getFluid(heldItem);

		// If the bucket is full, call the super method to try and empty it
		if (fluidStack != null) {
			return super.onItemRightClick(world, player, hand);
		}

		// If the bucket is empty, try and fill it
		final RayTraceResult target = this.rayTrace(world, player, true);

		if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult<>(EnumActionResult.PASS, heldItem);
		}

		final BlockPos pos = target.getBlockPos();

		final ItemStack singleBucket = heldItem.copy();
		singleBucket.setCount(1);

		final FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, pos, target.sideHit);
		if (filledResult.isSuccess()) {
			final ItemStack filledBucket = filledResult.result;

			if (player.capabilities.isCreativeMode)
				return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);

			heldItem.shrink(1);
			if (heldItem.isEmpty())
				return new ActionResult<>(EnumActionResult.SUCCESS, filledBucket);

			ItemHandlerHelper.giveItemToPlayer(player, filledBucket);

			return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
		}

		return new ActionResult<>(EnumActionResult.PASS, heldItem);
	}

	@Override
	public String getUnlocalizedNameInefficiently (final ItemStack stack) {
		final FluidStack fluidStack = getFluid(stack);
		return super.getUnlocalizedNameInefficiently(stack) + (fluidStack == null ? "" : "_filled");
	}

	@Override
	public String getItemStackDisplayName (final ItemStack stack) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			return Translation.localize(getUnlocalizedNameInefficiently(stack) + ".name");
		}
		return getRegistryName().toString();
	}

	@Override
	public ItemStack getEmpty () {
		return EMPTY;
	}

	@Nullable
	@Override
	public FluidStack getFluid (final ItemStack container) {
		return FluidUtil.getFluidContained(container);
	}

	@Override
	public void registerModels () {
		final ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomMeshDefinition(this, stack -> loc);
		ModelBakery.registerItemVariants(this, loc);
	}

	@Override
	public ICapabilityProvider initCapabilities (final ItemStack stack, final NBTTagCompound nbt) {
		// FluidBucketWrapper only works with Forge's UniversalBucket instance, use a different IFluidHandlerItem
		// implementation instead
		return new FluidHandler(stack, getCapacity());
	}

	public static class FluidHandler extends FluidHandlerItemStackSimple {

		public FluidHandler (final ItemStack container, final int capacity) {
			super(container, capacity);
		}

		@Override
		public boolean canFillFluidType (final FluidStack fluid) {
			return fluid.getFluid() == FluidRegistry.WATER;
		}
	}

	@Override
	public void onRegisterDescription (final JeiDescriptionRegistry registry) {
		registry.add(this);
	}
}
