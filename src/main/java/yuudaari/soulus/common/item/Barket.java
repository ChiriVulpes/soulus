package yuudaari.soulus.common.item;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.util.IModItem;

public class Barket extends UniversalBucket implements IModItem {
	private final ItemStack EMPTY = new ItemStack(this);

	public int maxDamage = 100;

	public Barket() {
		super(1000, ItemStack.EMPTY, true);
		setCreativeTab(CreativeTab.INSTANCE);
		setRegistryName(Soulus.MODID + ":" + getName());
		setUnlocalizedName(getRegistryName().toString());
		setMaxDamage(maxDamage);
	}

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return getFluid(itemStack) == null ? 120 : 0;
	}

	@Override
	public String getName() {
		return "barket";
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		FluidStack fluid = getFluid(stack);
		if (fluid != null) {
			stack.damageItem(1, (EntityLivingBase) entityIn);
			if (stack.getCount() == 0 && entityIn instanceof EntityPlayer && !worldIn.isRemote) {
				stack.setCount(1);
				FluidUtil.tryPlaceFluid((EntityPlayer) entityIn, worldIn, entityIn.getPosition(), stack, fluid);
				stack.setCount(0);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !fluidsEqual(oldStack, newStack);
	}

	public boolean fluidsEqual(ItemStack s1, ItemStack s2) {
		FluidStack f1 = getFluid(s1);
		FluidStack f2 = getFluid(s2);
		return f1 == null ? f2 == null : f1.equals(f2);
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (!this.isInCreativeTab(tab))
			return;

		subItems.add(EMPTY);

		FluidStack fs = new FluidStack(FluidRegistry.WATER, getCapacity());
		ItemStack stack = new ItemStack(this);
		IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
				null);
		if (fluidHandler != null && fluidHandler.fill(fs, true) == fs.amount) {
			ItemStack filled = fluidHandler.getContainer();
			subItems.add(filled);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);
		FluidStack fluidStack = getFluid(heldItem);

		// If the bucket is full, call the super method to try and empty it
		if (fluidStack != null) {
			return super.onItemRightClick(world, player, hand);
		}

		// If the bucket is empty, try and fill it
		RayTraceResult target = this.rayTrace(world, player, true);

		if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult<>(EnumActionResult.PASS, heldItem);
		}

		BlockPos pos = target.getBlockPos();

		ItemStack singleBucket = heldItem.copy();
		singleBucket.setCount(1);

		FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, pos, target.sideHit);
		if (filledResult.isSuccess()) {
			ItemStack filledBucket = filledResult.result;

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
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		FluidStack fluidStack = getFluid(stack);
		return super.getUnlocalizedNameInefficiently(stack) + (fluidStack == null ? "" : "_filled");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			return I18n.format(getUnlocalizedNameInefficiently(stack) + ".name");
		}
		return getRegistryName().toString();
	}

	@Override
	public ItemStack getEmpty() {
		return EMPTY;
	}

	@Nullable
	@Override
	public FluidStack getFluid(ItemStack container) {
		return FluidUtil.getFluidContained(container);
	}

	@Override
	public void registerModels() {
		ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomMeshDefinition(this, stack -> loc);
		ModelBakery.registerItemVariants(this, loc);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		// FluidBucketWrapper only works with Forge's UniversalBucket instance, use a different IFluidHandlerItem implementation instead
		return new FluidHandler(stack, getCapacity());
	}

	public static class FluidHandler extends FluidHandlerItemStackSimple {
		public FluidHandler(final ItemStack container, final int capacity) {
			super(container, capacity);
		}

		@Override
		public boolean canFillFluidType(final FluidStack fluid) {
			return fluid.getFluid() == FluidRegistry.WATER;
		}
	}
}