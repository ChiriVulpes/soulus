package yuudaari.soulus.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;

public class Unloader extends ModBlock {

	public Unloader () {
		super("unloader", new Material(MapColor.STONE));
		setHasItem();
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.STONE);
		setHardness(3F);
		setHasDescription();
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return true;
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass () {
		return UnloaderTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity (World worldIn, IBlockState blockState) {
		return new UnloaderTileEntity();
	}

	public static class UnloaderTileEntity extends TileEntity implements ITickable {

		public ItemHandler itemHandler = new ItemHandler(this);

		@Override
		public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
		}

		@Override
		public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
			return super.getCapability(capability, facing);
		}

		@Override
		public void readFromNBT (NBTTagCompound compound) {
			itemHandler = new ItemHandler(this);
			itemHandler.deserializeNBT(compound.getCompoundTag("item_handler"));
			super.readFromNBT(compound);
		}

		@Override
		public NBTTagCompound writeToNBT (NBTTagCompound compound) {
			compound.setTag("item_handler", itemHandler.serializeNBT());
			return super.writeToNBT(compound);
		}

		@Override
		public void update () {
			ItemStack stack = itemHandler.getStackInSlot(0);
			if (!stack.isEmpty()) {
				UpgradeableBlockTileEntity.dispenseItem(stack, world, pos, EnumFacing.DOWN);
				itemHandler.setStackInSlot(0, ItemStack.EMPTY);
			}
		}

		public static class ItemHandler extends ItemStackHandler {

			private UnloaderTileEntity te;

			public ItemHandler (UnloaderTileEntity te) {
				this.te = te;
			}

			@Override
			protected void onContentsChanged (int slot) {
				te.markDirty();
			}
		}


	}

}
