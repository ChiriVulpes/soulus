package yuudaari.soulus.common.block.composer.cell_mode;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.recipe.RecipeSledgehammer;
import yuudaari.soulus.common.util.ItemStackMutable;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.Translation;

/**
 * Auto-hammer
 */
@ConfigInjected(Soulus.MODID)
public class CellModeAutoHammer extends ComposerCellTileEntity.Mode {

	@Inject public static ConfigComposerCell CONFIG;
	@Inject public static ConfigBoneTypes CONFIG_BONE_TYPES;

	@Nullable public ItemStack storedInputType;
	public int storedInputQuantity;
	public int ticks = 0;
	private RecipeSledgehammer recipe = null;

	@Override
	public String getName () {
		return "auto_hammer";
	}

	@Override
	public boolean isActive () {
		return !cell.isConnected() //
			&& cell.storedItem != null && cell.storedItem.getItem() instanceof Sledgehammer //
			&& cell.storedQuantity == 1;
	}

	@Override
	public boolean isLockingStoredItem () {
		return storedInputType != null && storedInputQuantity > 0;
	}

	private Sledgehammer getSledgehammer () {
		return (Sledgehammer) cell.storedItem.getItem();
	}

	private double getHammerSpeed () {
		switch (getSledgehammer().type) {
			case ENDERSTEEL:
				return CONFIG.autoHammerTicksPerHammerEndersteel;
			case ENDERSTEEL_DARK:
				return CONFIG.autoHammerTicksPerHammerEndersteelDark;
			case NIOBIUM:
				return CONFIG.autoHammerTicksPerHammerNiobium;
			default:
				return CONFIG.autoHammerTicksPerHammerIron;
		}
	}

	public RecipeSledgehammer getRecipe (final ItemStack stack) {
		return ForgeRegistries.RECIPES.getValuesCollection()
			.stream()
			.filter(r -> r instanceof RecipeSledgehammer)
			.map(r -> (RecipeSledgehammer) r)
			.filter(r -> r.getInput().test(stack))
			.findFirst()
			.orElse(null);
	}

	@Override
	public int getMaxContainedQuantityForOtherModes (final ItemStack stack) {
		if (!isLockingStoredItem())
			return super.getMaxContainedQuantityForOtherModes(stack);

		return 1;
	}

	@Override
	public boolean tryInsert (final ItemStackMutable stack, final int requestedQuantity, final boolean isPulling) {
		if (!isPulling && stack.getItem() instanceof Sledgehammer) {
			final ItemStack replaceStack = cell.storedItem.copy();
			replaceStack.setCount(cell.storedQuantity);

			cell.storedItem = stack.getImmutable().copy();
			cell.storedQuantity = stack.getCount();
			cell.onChangeItem();
			cell.blockUpdate();

			stack.getImmutable().shrink(requestedQuantity);

			stack.replace(replaceStack);
			return true;
		}

		if (storedInputType != null && !ComposerCellTileEntity.areItemStacksEqual(stack.getImmutable(), storedInputType))
			return false;

		if (storedInputType == null)
			recipe = getRecipe(stack.getImmutable());

		if (recipe == null)
			return false;

		final int canStillBeInsertedQuantity = CONFIG.autoHammerMaxItemBuffer - (storedInputType == null ? 0 : storedInputQuantity);
		final int insertQuantity = Math.min(requestedQuantity, canStillBeInsertedQuantity);

		if (storedInputType == null) {
			storedInputType = stack.copy();
			storedInputType.setCount(1);
			storedInputQuantity = insertQuantity;
		} else {
			storedInputQuantity += insertQuantity;
		}

		if (storedInputType == null)
			ticks = 0;

		stack.shrink(insertQuantity);

		cell.blockUpdate();

		return true;
	}

	@Override
	public boolean tryExtract (final List<ItemStack> extracted) {
		if (storedInputType == null || storedInputQuantity <= 0)
			return false;

		ComposerCellTileEntity.addItemStackToList(storedInputType, extracted, storedInputQuantity);
		storedInputQuantity = 0;
		storedInputType = null;
		return true;
	}

	@Override
	public void update () {
		if (storedInputType == null || storedInputQuantity <= 0)
			return;

		ticks++;

		final World world = cell.getWorld();
		final BlockPos pos = cell.getPos();

		if (recipe == null || !recipe.getInput().test(storedInputType))
			return;

		final double autoHammerPerTick = getHammerSpeed();

		if (ticks < autoHammerPerTick)
			// we haven't reached hammer time yet
			return;

		if (ticks % 2 > 0)
			// we only update every two ticks no matter what it's set to, for efficiency
			return;

		final int hammerQuantity = (int) Math.min(ticks / autoHammerPerTick, storedInputQuantity);
		storedInputQuantity -= hammerQuantity;

		final List<ItemStack> outputs = new ArrayList<>();
		ComposerCellTileEntity.addItemStackToList(recipe.getRecipeOutput(), outputs, hammerQuantity);

		if (cell.storedItem.attemptDamageItem(hammerQuantity, world.rand, null)) {
			cell.storedQuantity--;
			if (cell.storedQuantity <= 0) {
				cell.storedItem = null;
				cell.onChangeItem();
				cell.blockUpdate();
				ComposerCellTileEntity.addItemStackToList(storedInputType, outputs, storedInputQuantity);
				storedInputQuantity = 0;
			}
		}

		ticks = 0;

		for (final ItemStack resultStack : outputs)
			UpgradeableBlockTileEntity.dispenseItem(resultStack, world, pos, EnumFacing.DOWN);

		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GRAVEL_HIT, SoundCategory.BLOCKS, 0.5F + 0.5F * (float) world.rand
			.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

		ComposerCellTileEntity.itemParticles(world, pos, Item.getIdFromItem(storedInputType.getItem()), hammerQuantity);

		if (storedInputQuantity <= 0)
			storedInputType = null;

		// update tooltip
		cell.blockUpdate();

		Advancements.COMPOSER_CELL_AUTO_HAMMER_TRIGGER.trigger(cell.getOwner(), null);
	}


	////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT (final NBTTagCompound compound) {
		compound.setInteger("stored_autohammer_quantity", storedInputQuantity);
		if (storedInputQuantity > 0 && storedInputType != null)
			compound.setTag("stored_autohammer_item", storedInputType.writeToNBT(new NBTTagCompound()));

		compound.setInteger("auto_hammer_ticks", ticks);
	}

	@Override
	public void onReadFromNBT (final NBTTagCompound compound) {
		storedInputQuantity = compound.getInteger("stored_autohammer_quantity");
		storedInputType = storedInputQuantity <= 0 ? null : new ItemStack(compound.getCompoundTag("stored_autohammer_item"));
		recipe = getRecipe(storedInputType);

		ticks = compound.getInteger("auto_hammer_ticks");
	}


	////////////////////////////////////
	// Rendering
	//

	private static final Range SWING_SPEED = new Range(10, 60);

	@Override
	public double getSwingSpeed () {
		return SWING_SPEED.get(getSledgehammer().type.ordinal() / (double) Sledgehammer.Type.values().length);
	}


	////////////////////////////////////
	// Tooltip
	//

	@Override
	public void onWailaTooltipHeader (final List<String> currentTooltip, final EntityPlayer player) {
		currentTooltip.add(new Translation("waila." + Soulus.MODID + ":composer_cell.auto_hammer_contained_hammer")
			.addArgs(cell.storedItem.getDisplayName())
			.addArgs(new Translation("waila." + Soulus.MODID + ":composer_cell.auto_hammer_tier_" + (getSledgehammer().type.isMaxTier() ? "max" : "n"))
				.get(getSledgehammer().type.ordinal() + 1))
			.get());

		if (storedInputType == null || storedInputQuantity <= 0)
			currentTooltip.add(Translation.localize("waila." + Soulus.MODID + ":composer_cell.auto_hammer"));
		else
			currentTooltip.add(new Translation("waila." + Soulus.MODID + ":composer_cell.auto_hammer_contained_items")
				.addArgs(storedInputQuantity, CONFIG.autoHammerMaxItemBuffer, storedInputType.getDisplayName())
				.get());

		if (cell.storedItem.getItemDamage() > 0)
			currentTooltip.add(new Translation("waila." + Soulus.MODID + ":composer_cell.auto_hammer_durability")
				.addArgs(cell.storedItem.getMaxDamage() - cell.storedItem.getItemDamage(), cell.storedItem.getMaxDamage())
				.get());
	}

	@Override
	public boolean allowRenderingItemInTooltip () {
		return false;
	}
}
