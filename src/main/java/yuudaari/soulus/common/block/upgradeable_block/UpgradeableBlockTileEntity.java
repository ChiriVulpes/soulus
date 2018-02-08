package yuudaari.soulus.common.block.upgradeable_block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;

public abstract class UpgradeableBlockTileEntity extends TileEntity implements ITickable {

	public UpgradeableBlockTileEntity () {
		this.onUpdateUpgrades(false);
	}

	public abstract UpgradeableBlock<? extends UpgradeableBlockTileEntity> getBlock ();

	public Map<IUpgrade, Integer> upgrades = new HashMap<>();
	{
		for (IUpgrade upgrade : getBlock().getUpgrades()) {
			upgrades.put(upgrade, 0);
		}
	}

	public Stack<IUpgrade> insertionOrder = new Stack<>();

	public void addUpgradeStacksToList (List<ItemStack> list) {
		for (Map.Entry<IUpgrade, Integer> upgrade : upgrades.entrySet()) {
			upgrade.getKey().addItemStackToList(this, list, (int) upgrade.getValue());
		}
	}

	public IUpgrade getUpgradeForItem (ItemStack stack) {
		for (Map.Entry<IUpgrade, Integer> upgradeEntry : upgrades.entrySet()) {
			IUpgrade upgrade = upgradeEntry.getKey();
			if (upgrade.isItemStackForTileEntity(stack, this))
				return upgrade;
		}

		return null;
	}

	public final void insertUpgrade (ItemStack stack, IUpgrade upgrade, int quantity) {
		this.insertionOrder.remove(upgrade);
		this.insertionOrder.push(upgrade);

		int currentQuantity = upgrades.get(upgrade);
		int maxQuantity = upgrade.getMaxQuantity();
		int insertQuantity = Math.min(quantity, maxQuantity - currentQuantity);

		if (insertQuantity > 0) {
			int newQuantity = currentQuantity + insertQuantity;

			upgrades.put(upgrade, newQuantity);
			onInsertUpgrade(stack, upgrade, newQuantity);

			stack.shrink(insertQuantity);

			onUpdateUpgrades(false);
		}

		blockUpdate();
	}

	public void onInsertUpgrade (ItemStack stack, IUpgrade upgrade, int newQuantity) {}

	public final IUpgrade popLastUpgrade () {
		return insertionOrder.size() == 0 ? null : insertionOrder.pop();
	}

	public int removeUpgrade (IUpgrade upgrade) {
		int result = upgrades.get(upgrade);
		upgrades.put(upgrade, 0);

		onUpdateUpgrades(false);
		blockUpdate();
		return result;
	}

	public void clear () {
		for (IUpgrade upgrade : getBlock().getUpgrades()) {
			upgrades.put(upgrade, 0);
		}

		onUpdateUpgrades(false);
		blockUpdate();
	}

	public static IInventory getFacingInventory (World world, BlockPos pos, EnumFacing facing) {
		BlockPos facingPos = pos.offset(facing);
		return TileEntityHopper.getInventoryAtPosition(world, (double) facingPos
			.getX(), (double) facingPos.getY(), (double) facingPos.getZ());
	}

	public static ItemStack insertItem (ItemStack stack, World world, BlockPos pos, EnumFacing facing) {
		IInventory facingInventory = getFacingInventory(world, pos, facing);
		if (facingInventory == null) return stack;

		ItemStack result = TileEntityHopper
			.putStackInInventoryAllSlots(null, facingInventory, stack.copy(), facing.getOpposite());
		stack.setCount(result.getCount());
		return result;
	}

	public static void dispenseItem (ItemStack stack, World world, BlockPos pos, EnumFacing facing) {
		if (!stack.isEmpty()) {
			IInventory facingInventory = getFacingInventory(world, pos, facing);

			if (facingInventory != null) {
				insertItem(stack, world, pos, facing);
				return;
			}

			double d0 = pos.getX() + 0.5 + facing.getFrontOffsetX() / 1.5;
			double d1 = pos.getY() + 0.5 + facing.getFrontOffsetY() / 1.5;
			double d2 = pos.getZ() + 0.5 + facing.getFrontOffsetZ() / 1.5;

			if (facing.getAxis() == EnumFacing.Axis.Y) {
				d1 = d1 - 0.125D;
			} else {
				d1 = d1 - 0.15625D;
			}

			EntityItem itemEntity = new EntityItem(world, d0, d1, d2, stack);
			double d3 = world.rand.nextDouble() * 0.1D + 0.2D;
			itemEntity.motionX = facing.getFrontOffsetX() * d3;
			itemEntity.motionY = facing.getFrontOffsetY() * d3;
			itemEntity.motionZ = facing.getFrontOffsetZ() * d3;
			double speed = 1.5;
			itemEntity.motionX += world.rand.nextGaussian() * 0.0075D * speed;
			itemEntity.motionY += world.rand.nextGaussian() * 0.0075D * speed;
			itemEntity.motionZ += world.rand.nextGaussian() * 0.0075D * speed;
			world.spawnEntity(itemEntity);
		}
	}

	/////////////////////////////////////////
	// Events
	//

	public void onUpdateUpgrades (boolean readFromNBT) {}

	public void onReadFromNBT (NBTTagCompound compound) {}

	public void onWriteToNBT (NBTTagCompound compound) {}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public final void readFromNBT (NBTTagCompound compound) {
		super.readFromNBT(compound);

		IUpgrade[] upgrades = getBlock().getUpgrades();

		NBTTagCompound upgradeTag = compound.getCompoundTag("upgrades");
		for (IUpgrade upgrade : upgrades) {
			this.upgrades.put(upgrade, upgradeTag.getInteger(upgrade.getName()));
		}

		this.insertionOrder = new Stack<>();
		NBTTagList value = (NBTTagList) compound.getTag("insertion_order");
		for (NBTBase s : value) {
			if (s instanceof NBTTagString) {
				String str = ((NBTTagString) s).getString();
				for (IUpgrade u : upgrades) {
					if (u.getName().equals(str)) {
						this.insertionOrder.add(u);
					}
				}
			}
		}

		this.onUpdateUpgrades(true);

		onReadFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound) {
		super.writeToNBT(compound);

		onWriteToNBT(compound);

		NBTTagList list = new NBTTagList();
		for (IUpgrade upgrade : insertionOrder) {
			list.appendTag(new NBTTagString(upgrade.getName()));
		}
		compound.setTag("insertion_order", list);

		IUpgrade[] upgrades = getBlock().getUpgrades();

		NBTTagCompound upgradeTag = new NBTTagCompound();
		for (IUpgrade upgrade : upgrades) {
			upgradeTag.setInteger(upgrade.getName(), this.upgrades.get(upgrade));
		}
		compound.setTag("upgrades", upgradeTag);

		return compound;
	}

	/////////////////////////////////////////
	// Block Updates
	//

	@Override
	public final NBTTagCompound getUpdateTag () {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket () {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public final void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public final void blockUpdate () {
		if (world != null) {
			IBlockState blockState = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, blockState, blockState, 3);
			markDirty();
		}
	}
}
