package yuudaari.soulus.common.block.composer;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposerCell;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.item.ConfigBoneChunks;
import yuudaari.soulus.common.misc.BoneChunks;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.ComposerCellMarrow;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.registration.ItemRegistry;

@ConfigInjected(Soulus.MODID)
public class ComposerCellTileEntity extends HasRenderItemTileEntity {

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigComposerCell CONFIG;
	@Inject public static ConfigBoneTypes CONFIG_BONE_TYPES;
	@Inject public static ConfigBoneChunks CONFIG_BONE_CHUNKS;

	public ChangeItemHandler changeItemHandler;
	public BlockPos composerLocation;
	public int changeComposerCooldown = 0;
	public byte slot = -1;

	@Nullable public ItemStack storedItem;
	public int storedQuantity;

	@Override
	public ComposerCell getBlock () {
		return BlockRegistry.COMPOSER_CELL;
	}

	@Override
	public boolean isMarrowingMode () {
		return storedItem.getItem() == ItemRegistry.GEAR_OSCILLATING && storedQuantity == 1 && composerLocation == null;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean shouldCheckSignal = true;
	private boolean lastSignalIn = false;

	@Override
	public void update () {
		changeComposerCooldown--;

		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation = itemRotation + 0.05F + diff * 0.8;

		if (!world.isRemote && storedQuantity < CONFIG.maxQuantity)
			pullItems();

		if (!shouldCheckSignal || world.isRemote)
			return;

		shouldCheckSignal = false;

		final boolean signalIn = world.isBlockIndirectlyGettingPowered(pos) > 0;
		if (signalIn && !lastSignalIn && storedItem != null) {
			UpgradeableBlockTileEntity.dispenseItem(storedItem.copy(), world, pos, EnumFacing.DOWN);
			storedQuantity--;
			if (storedQuantity == 0) storedItem = null;
			onChangeItem();
			blockUpdate();
		}

		lastSignalIn = signalIn;
	}

	public void onChangeItem () {
		if (composerLocation == null)
			return;
		TileEntity te = world.getTileEntity(composerLocation);
		if (te == null || !(te instanceof ComposerTileEntity) || !((ComposerTileEntity) te).isConnected())
			return;

		if (changeItemHandler != null)
			changeItemHandler.handle(this);
	}

	public void onChangeItem (ChangeItemHandler handler) {
		changeItemHandler = handler;
	}

	public static interface ChangeItemHandler {

		public Boolean handle (ComposerCellTileEntity ccte);
	}

	public boolean pullItems () {

		for (final EntityItem item : getCaptureItems()) {
			if (item.ticksExisted < 2) continue;
			final ItemStack stack = item.getItem();
			if (tryInsert(stack, stack.getCount())) return true;
		}

		return false;
	}

	public boolean tryInsert (final ItemStack stack, final int requestedQuantity) {
		if (stack == null || stack.isEmpty()) return false;

		ItemStack currentStack = storedItem;
		if (currentStack != null && currentStack.isEmpty()) currentStack = storedItem = null;

		if (currentStack == null || areItemStacksEqual(stack, currentStack)) {
			final int canStillBeInsertedQuantity = CONFIG.maxQuantity - (currentStack == null ? 0 : storedQuantity);
			final int insertQuantity = Math.min(requestedQuantity, canStillBeInsertedQuantity);

			if (currentStack == null) {
				storedItem = stack.copy();
				storedItem.setCount(1);
				storedQuantity = insertQuantity;
			} else {
				storedQuantity += insertQuantity;
			}

			stack.shrink(insertQuantity);
			onChangeItem();
			blockUpdate();

			return true;

		} else if (composerLocation == null) {
			if (currentStack.getItem() instanceof IFillableWithEssence && storedQuantity == 1 //
				&& (stack.getItem() == ItemRegistry.ESSENCE || stack.getItem() == ItemRegistry.ASH)) {
				// auto-fill items with essence or ash

				final IFillableWithEssence fillable = (IFillableWithEssence) currentStack.getItem();
				final int insertQuantity = fillable.fill(currentStack, stack, requestedQuantity);
				if (insertQuantity > 0) {
					stack.shrink(insertQuantity);
					blockUpdate();
				}

			} else if (currentStack.getItem() == ItemRegistry.GEAR_OSCILLATING) {
				final Item boneChunk = stack.getItem();
				final ConfigBoneType boneType = CONFIG_BONE_TYPES.getFromChunk(boneChunk.getRegistryName().toString());
				if (boneType != null) {
					// auto-marrow	

					final Collection<ItemStack> results = BoneChunks.getMarrowingDrops(world.rand, boneType.name, stack.getCount());
					stack.shrink(stack.getCount());

					for (final ItemStack resultStack : results)
						dispenseItem(resultStack, world, pos, EnumFacing.DOWN);

					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GRAVEL_HIT, SoundCategory.BLOCKS, 0.5F + 0.5F * (float) world.rand
						.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

					SoulsPacketHandler.INSTANCE
						.sendToAllAround(new ComposerCellMarrow(this, boneChunk), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
				}
			}
		}

		return false;
	}

	/////////////////////////////////////////
	// Utility
	//

	public static boolean areItemStacksEqual (ItemStack stackA, ItemStack stackB) {
		if (stackA.getItem() != stackB.getItem()) {
			return false;
		} else if (stackA.getItemDamage() != stackB.getItemDamage()) {
			return false;
		} else if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
			return false;
		} else {
			return (stackA.getTagCompound() == null || stackA.getTagCompound()
				.equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
		}
	}

	public List<EntityItem> getCaptureItems () {
		return world
			.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos)
				.offset(new BlockPos(EnumFacing.UP.getDirectionVec()))
				.contract(0, 0.8, 0), EntitySelectors.IS_ALIVE);
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		compound.setBoolean("has_composer", composerLocation != null);

		if (composerLocation != null) {
			compound.setInteger("composer_x", composerLocation.getX());
			compound.setInteger("composer_y", composerLocation.getY());
			compound.setInteger("composer_z", composerLocation.getZ());
		}

		compound.setInteger("stored_quantity", storedQuantity);

		if (storedQuantity > 0) {
			compound.setTag("stored_item", storedItem.writeToNBT(new NBTTagCompound()));
		}

		compound.setByte("slot", slot);
	}

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		if (compound.getBoolean("has_composer")) {
			composerLocation = new BlockPos(compound.getInteger("composer_x"), compound
				.getInteger("composer_y"), compound.getInteger("composer_z"));
		}

		storedQuantity = compound.getInteger("stored_quantity");
		if (storedQuantity > 0) {
			storedItem = new ItemStack(compound.getCompoundTag("stored_item"));
		} else {
			storedItem = null;
		}

		slot = compound.getByte("slot");
	}

	/////////////////////////////////////////
	// Renderer
	//

	private double itemRotation = Math.random() * 360;
	private double prevItemRotation = 0;

	@Override
	public double getItemRotation () {
		return itemRotation;
	}

	@Override
	public double getPrevItemRotation () {
		return prevItemRotation;
	}

	@Override
	@Nullable
	public ItemStack getStoredItem () {
		return storedItem;
	}

	@SideOnly(Side.CLIENT)
	public static void marrowParticles (final World world, final BlockPos pos, final int chunk) {
		for (int i = 0; i < CONFIG_BONE_CHUNKS.particleCount; ++i) {
			// Vec3d v = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			// v = v.rotatePitch(-player.rotationPitch * 0.017453292F);
			// v = v.rotateYaw(-player.rotationYaw * 0.017453292F);
			// final double d0 = (double) (-world.rand.nextFloat()) * 0.6D - 0.3D;
			// Vec3d v2 = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			// v2 = v2.rotatePitch(-player.rotationPitch * 0.017453292F);
			// v2 = v2.rotateYaw(-player.rotationYaw * 0.017453292F);
			// v2 = v2.addVector();

			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0, 0, 0, chunk);
		}
	}
}
