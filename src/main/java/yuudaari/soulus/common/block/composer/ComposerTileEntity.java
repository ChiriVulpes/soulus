package yuudaari.soulus.common.block.composer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;

public class ComposerTileEntity extends HasRenderItemTileEntity {

	private boolean isConnected = false;
	private float timeTillCraft = 5;
	private float lastTimeTillCraft = 10;

	@Override
	public Composer getBlock() {
		return Composer.INSTANCE;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean isConnected() {
		return isConnected;
	}

	/////////////////////////////////////////
	// Update
	//

	private double activationAmount() {
		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) {
			return 0;
		}

		double activationAmount = 0;

		for (EntityPlayer player : world.playerEntities) {

			if (EntitySelectors.NOT_SPECTATING.apply(player)) {
				double d0 = player.getDistanceSqToCenter(pos);

				double nearAmt = (d0 / (20));
				activationAmount += Math.max(0, (1 - (nearAmt * nearAmt)) * 2);
			}
		}

		return activationAmount;
	}

	@Override
	public void update() {
		if (world.isRemote) {
			double activationAmount = activationAmount();
			updateRenderer(activationAmount);

		} else {
			validateStructure();

			if (needsRecipeRefresh) {
				refreshRecipe();
			}
		}
	}

	private Map<BlockPos, Byte> cellMap = new HashMap<>();

	public void validateStructure() {
		IBlockState state = world.getBlockState(pos);
		EnumFacing currentDirection = state.getValue(Composer.FACING);

		EnumFacing direction = getBlock().validateStructure(world, pos, currentDirection);
		isConnected = direction != null;

		boolean changedState = false;

		if (isConnected && currentDirection != direction) {
			state = state.withProperty(Composer.FACING, direction);
			changedState = true;
		}

		if (state.getValue(Composer.CONNECTED) != isConnected) {
			state = state.withProperty(Composer.CONNECTED, isConnected);
			changedState = true;

			BlockPos center = direction == null ? null : pos.offset(direction, -3);
			BlockPos x = direction == null ? null : BlockPos.ORIGIN.offset(direction, 1);
			BlockPos z = direction == null ? null : BlockPos.ORIGIN.offset(direction.rotateY(), 1);
			BlockPos topLeft = direction == null ? null : offset(offset(pos, x, -4), z, -1);

			if (isConnected) {
				cellMap.clear();
				for (int iz = 0; iz < 3; iz++) {
					for (int ix = 0; ix < 3; ix++) {
						BlockPos cellPos = offset(offset(topLeft, x, ix), z, iz);
						cellMap.put(cellPos, (byte) (ix + iz * 3));
					}
				}
			}

			getBlock().structure.loopBlocks(world, pos, state.getValue(Composer.FACING),
					(BlockPos pos2, BlockValidator validator) -> {
						IBlockState currentState = world.getBlockState(pos2);

						if (currentState.getBlock() == ComposerCell.INSTANCE) {
							ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(pos2);
							BlockPos ccPos = ccte.getPos();
							world.setBlockState(ccPos,
									currentState.withProperty(ComposerCell.CELL_STATE,
											!isConnected ? ComposerCell.CellState.DISCONNECTED
													: ccPos.equals(center) ? ComposerCell.CellState.CONNECTED_CENTER
															: ComposerCell.CellState.CONNECTED_EDGE),
									3);

							ccte.composerLocation = isConnected ? pos : null;
							ccte.changeComposerCooldown = 20;
							Byte slot = cellMap.get(ccPos);
							ccte.slot = slot == null ? -1 : slot;
							ccte.blockUpdate();
							ccte.onChangeItem(isConnected ? this::updateCCTEItem : null);
						}

						return null;
					});

			needsRecipeRefresh = true;
		}

		if (changedState)
			world.setBlockState(pos, state, 3);
	}

	private BlockPos offset(BlockPos a, BlockPos b, double amt) {
		return a.add(b.getX() * amt, b.getY() * amt, b.getZ() * amt);
	}

	public float getCompositionPercent() {
		return (lastTimeTillCraft - timeTillCraft) / (float) lastTimeTillCraft;
	}

	public boolean loopComposerCells(ComposerCellHandler handler) {
		for (Map.Entry<BlockPos, Byte> composerCell : cellMap.entrySet()) {
			TileEntity te = world.getTileEntity(composerCell.getKey());
			if (te == null || !(te instanceof ComposerCellTileEntity))
				return false;

			ComposerCellTileEntity ccte = (ComposerCellTileEntity) te;
			Boolean result = handler.handle(ccte);
			if (result != null)
				return result;
		}
		return true;
	}

	public static interface ComposerCellHandler {
		public Boolean handle(ComposerCellTileEntity te);
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT(NBTTagCompound compound) {
		NBTTagCompound cellTag = new NBTTagCompound();
		for (Map.Entry<BlockPos, Byte> cell : cellMap.entrySet()) {
			NBTTagCompound posTag = new NBTTagCompound();
			posTag.setInteger("x", cell.getKey().getX());
			posTag.setInteger("y", cell.getKey().getY());
			posTag.setInteger("z", cell.getKey().getZ());
			cellTag.setTag(cell.getValue().toString(), posTag);
		}
		compound.setTag("cell_map", cellTag);

		Logger.info("write to nbt, has container: " + (container != null));

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			Logger.info("stored item: " + getStoredItem());
			compound.setTag("crafting_item", getStoredItem().writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void onReadFromNBT(NBTTagCompound compound) {
		Logger.info("read from nbt 3");

		NBTTagCompound cellTag = compound.getCompoundTag("cell_map");
		for (Integer slot = 0; slot < 9; slot++) {
			NBTTagCompound posTag = cellTag.getCompoundTag(slot.toString());
			cellMap.put(new BlockPos(posTag.getInteger("x"), posTag.getInteger("y"), posTag.getInteger("z")),
					(byte) (int) slot);
		}

		needsRecipeRefresh = true;

		Logger.info("read from nbt, has crafting item: " + compound.hasKey("crafting_item"));

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			renderItem = new ItemStack(compound.getCompoundTag("crafting_item"));
			Logger.info("render item: " + renderItem);
		}
	}

	/////////////////////////////////////////
	// Renderer
	//

	@SideOnly(Side.CLIENT)
	private double itemRotation = 0;
	@SideOnly(Side.CLIENT)
	private double prevItemRotation = 0;
	private ItemStack renderItem;

	@SideOnly(Side.CLIENT)
	public void updateRenderer(double activationAmount) {
		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation += activationAmount <= 0 ? //
				diff * 0.9 // ease rotation to a stop
				: 1.0F * getCompositionPercent() + diff * 0.8; // normal rotation
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getItemRotation() {
		return itemRotation;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getPrevItemRotation() {
		return prevItemRotation;
	}

	@Override
	public ItemStack getStoredItem() {
		if (renderItem != null)
			return renderItem;
		if (container == null)
			return ItemStack.EMPTY;
		return container.craftResult.getStackInSlot(0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldComplexRotate() {
		return true;
	}

	/////////////////////////////////////////
	// Recipe
	//

	private static class ComposerContainer extends Container {
		public InventoryCrafting craftingMatrix;
		public InventoryCraftResult craftResult;
		private final World world;
		private final EntityPlayer player;

		public ComposerContainer(World world, EntityPlayer player) {
			this.world = world;
			this.player = player;
			this.craftingMatrix = new InventoryCrafting(this, 3, 3);
			this.craftResult = new InventoryCraftResult();
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}

		@Override
		public void onCraftMatrixChanged(IInventory inventoryIn) {
			this.slotChangedCraftingGrid(this.world, this.player, this.craftingMatrix, this.craftResult);
		}

		@Override
		protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix,
				InventoryCraftResult craftResult) {

			Logger.info("slot changed");
			if (!world.isRemote) {
				ItemStack stack = ItemStack.EMPTY;
				IRecipe recipe = CraftingManager.findMatchingRecipe(craftingMatrix, world);
				Logger.info("recipe " + recipe);

				if (recipe != null) {
					craftResult.setRecipeUsed(recipe);
					stack = recipe.getCraftingResult(craftingMatrix);
				}

				Logger.info("recipe result " + stack);
				craftResult.setInventorySlotContents(0, stack);
			}
		}
	}

	private FakePlayer fakePlayer;
	private ComposerContainer container;
	private UUID uuid;
	private boolean needsRecipeRefresh = true;

	public void refreshRecipe() {
		needsRecipeRefresh = false;

		if (container == null) {
			refreshContainer();
		}

		if (isConnected) {
			loopComposerCells(ccte -> this.updateCCTEItem(ccte, false));
		} else {
			container.craftResult.clear();
		}

		blockUpdate();
	}

	public void refreshContainer() {
		uuid = UUID.randomUUID();
		fakePlayer = new FakePlayer((WorldServer) world, new GameProfile(uuid, "composer_tile_entity"));
		container = new ComposerContainer(world, fakePlayer);
	}

	public Boolean updateCCTEItem(ComposerCellTileEntity ccte, boolean blockUpdate) {
		ccte.onChangeItem(this::updateCCTEItem);
		Logger.info("update slot " + ccte.slot + ", stored item: " + ccte.storedItem);
		container.craftingMatrix.setInventorySlotContents(ccte.slot,
				ccte.storedItem == null ? ItemStack.EMPTY : ccte.storedItem);

		if (blockUpdate)
			blockUpdate();

		return null;
	}

	public Boolean updateCCTEItem(ComposerCellTileEntity ccte) {
		return updateCCTEItem(ccte, true);
	}
}