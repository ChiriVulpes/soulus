package yuudaari.soulus.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.Material;
import yuudaari.soulus.common.util.ModBlock;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.util.Logger;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public abstract class UpgradeableBlock extends ModBlock {

	/////////////////////////////////////////
	// Upgrades
	//
	public static interface IUpgrade {
		public ItemStack getItemStack(int quantity);

		public default void addItemStackToList(List<ItemStack> list, int quantity) {
			ItemStack item = getItemStack(1);
			int maxStackSize = item.getMaxStackSize();
			while (quantity > 0) {
				list.add(getItemStack(Math.min(maxStackSize, quantity)));
				quantity -= maxStackSize;
			}
		}

		public boolean isItemStack(ItemStack stack);

		public int getIndex();

		public String getName();

		public byte getMaxQuantity();

		public void setMaxQuantity(byte quantity);
	}

	public abstract IUpgrade[] getUpgrades();

	/////////////////////////////////////////
	// Serializer
	//

	public final JsonElement serialize() {
		return serializer.serialize(this);
	}

	public final Object deserialize(JsonElement from) {
		serializer.deserialize(from, this);
		return null;
	}

	public final Serializer<UpgradeableBlock> serializer = new Serializer<>();
	{
		serializer.otherHandlers.put("upgradeMaxCounts",
				new ManualSerializer(this::serializeMaxUpgrades, this::deserializeMaxUpgrades));
	}

	private final JsonElement serializeMaxUpgrades(Object from) {
		@SuppressWarnings("unchecked")
		Map<IUpgrade, Integer> upgrades = (Map<IUpgrade, Integer>) from;

		JsonObject result = new JsonObject();

		for (Map.Entry<IUpgrade, Integer> upgrade : upgrades.entrySet()) {
			String key = upgrade.getKey().getName().toLowerCase();
			result.addProperty(key, upgrade.getValue());
		}

		return result;
	}

	private final Object deserializeMaxUpgrades(JsonElement from, Object current) {
		if (from == null || !from.isJsonObject()) {
			Logger.warn("Max upgrades must be an object");
			return current;
		}

		@SuppressWarnings("unchecked")
		Map<IUpgrade, Integer> currentUpgrades = (Map<IUpgrade, Integer>) current;

		JsonObject upgrades = from.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : upgrades.entrySet()) {
			JsonElement val = entry.getValue();

			if (val == null || !val.isJsonPrimitive() || !val.getAsJsonPrimitive().isNumber()) {
				Logger.warn("Upgrade maximum must be an number");
				continue;
			}

			String key = entry.getKey();
			IUpgrade upgrade = null;
			for (IUpgrade checkUpgrade : getUpgrades()) {
				if (key.equalsIgnoreCase(checkUpgrade.getName())) {
					upgrade = checkUpgrade;
				}
			}
			if (upgrade == null) {
				Logger.warn("Upgrade type '" + key + "' is invalid");
				continue;
			}

			currentUpgrades.put(upgrade, val.getAsInt());
		}

		return current;
	}

	/////////////////////////////////////////
	// Constructor
	//

	public UpgradeableBlock(String name, Material material) {
		super(name, material);
		setHasItem();
		disableStats();
		registerWailaProvider(UpgradeableBlock.class);
	}

	public abstract UpgradeableBlock getInstance();

	/////////////////////////////////////////
	// Events
	//

	@SubscribeEvent
	public static final void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof UpgradeableBlock
				&& ((UpgradeableBlock) block).canActivateWithItem(event.getItemStack(), world, pos)) {
			event.setUseBlock(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static final void onBlockBreak(BlockEvent.BreakEvent event) {
		Block block = event.getState().getBlock();
		if (block instanceof UpgradeableBlock) {
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			((UpgradeableBlock) block).onBlockDestroy(world, pos, world.getTileEntity(pos), EnchantmentHelper
					.getEnchantmentLevel(Enchantments.FORTUNE, event.getPlayer().getHeldItemMainhand()));
		}
	}

	@Override
	public final void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		onBlockDestroy(world, pos, world.getTileEntity(pos));
	}

	public final void onBlockDestroy(World world, BlockPos pos, TileEntity tileEntity) {
		onBlockDestroy(world, pos, tileEntity, 0);
	}

	public final void onBlockDestroy(World world, BlockPos pos, TileEntity tileEntity, int fortune) {
		List<ItemStack> drops = getActualDrops(world, pos, world.getBlockState(pos), fortune);

		dropItems(world, drops, pos);
	}

	@Override
	public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack heldStack = player.getHeldItem(hand);

			if (heldStack.isEmpty()) {
				return onActivateEmptyHand(world, pos, player);
			} else {
				return onActivateInsert(world, pos, player, heldStack);
			}
		}

		return true;
	}

	public boolean onActivateEmptyHand(World world, BlockPos pos, EntityPlayer player) {
		return onActivateReturnLastUpgrade(world, pos, player);
	}

	public final boolean onActivateReturnLastUpgrade(World world, BlockPos pos, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return false;

		UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

		IUpgrade upgrade = ute.popLastUpgrade();

		if (upgrade == null)
			return false;

		int count = ute.removeUpgrade(upgrade);

		List<ItemStack> toReturn = new ArrayList<>();
		upgrade.addItemStackToList(toReturn, count);

		returnItemsToPlayer(world, toReturn, player);

		return true;
	}

	public boolean onActivateInsert(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		return onActivateInsertUpgrade(world, pos, player, stack);
	}

	public final boolean onActivateInsertUpgrade(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return false;

		UpgradeableBlockTileEntity ute = (UpgradeableBlockTileEntity) te;

		IUpgrade upgrade = ute.getUpgradeForItem(stack);
		if (upgrade == null)
			return false;

		int insertedQuantity = ute.insertUpgrade(upgrade, stack.getCount());
		stack.shrink(insertedQuantity);

		return true;
	}

	@Override
	public final void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	/////////////////////////////////////////
	// Utility
	//

	protected static void returnItemsToPlayer(World world, List<ItemStack> items, EntityPlayer player) {
		for (ItemStack item : items) {
			EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, item);
			dropItem.setNoPickupDelay();
			world.spawnEntity(dropItem);
		}
	}

	protected static void dropItems(World world, List<ItemStack> items, BlockPos pos) {
		for (ItemStack item : items) {
			EntityItem dropItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
			dropItem.setNoPickupDelay();
			world.spawnEntity(dropItem);
		}
	}

	public List<ItemStack> getActualDrops(World world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> result = new ArrayList<>();

		addBlockToList(result, world, pos);

		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof UpgradeableBlockTileEntity) {
			((UpgradeableBlockTileEntity) te).addUpgradeStacksToList(result);
		}

		return result;
	}

	public void addBlockToList(List<ItemStack> list, World world, BlockPos pos) {
		list.add(getItemStack());
	}

	public IUpgrade isUpgradeItem(ItemStack stack, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te == null || !(te instanceof UpgradeableBlockTileEntity))
			return null;

		return ((UpgradeableBlockTileEntity) te).getUpgradeForItem(stack);
	}

	public boolean canActivateWithItem(ItemStack stack, World world, BlockPos pos) {
		return isUpgradeItem(stack, world, pos) != null;
	}

	/////////////////////////////////////////
	// Tile Entity
	//

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public abstract UpgradeableBlockTileEntity createTileEntity(World world, IBlockState state);

	@Override
	public abstract Class<? extends UpgradeableBlockTileEntity> getTileEntityClass();

	public static abstract class UpgradeableBlockTileEntity extends TileEntity implements ITickable {

		public abstract UpgradeableBlock getBlock();

		public Map<IUpgrade, Byte> upgrades = new HashMap<>();
		{
			for (IUpgrade upgrade : getBlock().getUpgrades()) {
				upgrades.put(upgrade, (byte) 0);
			}
		}

		public Stack<IUpgrade> insertionOrder = new Stack<>();

		public void addUpgradeStacksToList(List<ItemStack> list) {
			for (Map.Entry<IUpgrade, Byte> upgrade : upgrades.entrySet()) {
				upgrade.getKey().addItemStackToList(list, (int) upgrade.getValue());
			}
		}

		public IUpgrade getUpgradeForItem(ItemStack stack) {
			for (Map.Entry<IUpgrade, Byte> upgradeEntry : upgrades.entrySet()) {
				IUpgrade upgrade = upgradeEntry.getKey();
				if (upgrade.isItemStack(stack))
					return upgrade;
			}

			return null;
		}

		public int insertUpgrade(IUpgrade upgrade, int quantity) {
			this.insertionOrder.push(upgrade);

			int currentQuantity = upgrades.get(upgrade);
			int maxQuantity = upgrade.getMaxQuantity();
			int insertQuantity = Math.min(quantity, maxQuantity - currentQuantity);
			int newQuantity = currentQuantity + insertQuantity;

			upgrades.put(upgrade, (byte) newQuantity);
			onInsertUpgrade(upgrade, newQuantity);

			return insertQuantity;
		}

		public void onInsertUpgrade(IUpgrade upgrade, int newQuantity) {
		}

		public IUpgrade popLastUpgrade() {
			return insertionOrder.size() == 0 ? null : insertionOrder.pop();
		}

		public int removeUpgrade(IUpgrade upgrade) {
			int result = upgrades.get(upgrade);
			upgrades.put(upgrade, (byte) 0);
			return result;
		}

		/////////////////////////////////////////
		// Events
		//

		public void onUpdateUpgrades() {
		}

		public void onReadFromNBT(NBTTagCompound compound) {
		}

		public void onWriteToNBT(NBTTagCompound compound) {
		}

		/////////////////////////////////////////
		// NBT
		//

		@Override
		public final void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);

			IUpgrade[] upgrades = getBlock().getUpgrades();

			NBTTagCompound upgradeTag = compound.getCompoundTag("upgrades");
			for (IUpgrade upgrade : upgrades) {
				this.upgrades.put(upgrade, upgradeTag.getByte(upgrade.getName()));
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

			onReadFromNBT(compound);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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
				upgradeTag.setByte(upgrade.getName(), this.upgrades.get(upgrade));
			}
			compound.setTag("upgrades", upgradeTag);

			return compound;
		}

		@Override
		public final NBTTagCompound getUpdateTag() {
			NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
			return nbt;
		}

		@Override
		public final SPacketUpdateTileEntity getUpdatePacket() {
			return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
		}

		@Override
		public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			readFromNBT(pkt.getNbtCompound());
		}
	}
}