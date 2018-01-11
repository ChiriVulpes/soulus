package yuudaari.soulus.common.block.composer;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.client.util.ParticleManager;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.composer.Composer.Upgrade;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.MobPoof;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ComposerTileEntity extends HasRenderItemTileEntity {

	private boolean isConnected = false;
	private float timeTillCraft = 5;
	private float lastTimeTillCraft = 10;
	private int activatingRange;
	private Range spawnDelay;
	private int signalStrength;

	@Override
	public Composer getBlock () {
		return Composer.INSTANCE;
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean isConnected () {
		return isConnected;
	}

	public boolean hasValidRecipe () {
		ItemStack storedItem = getStoredItem();
		return storedItem != null && !storedItem.isEmpty();
	}

	public int getSignalStrength () {
		return isConnected && hasValidRecipe() ? signalStrength : 0;
	}

	/////////////////////////////////////////
	// Update
	//

	public void reset () {
		this.resetTimer();
	}

	private void resetTimer () {
		resetTimer(true);
	}

	private void resetTimer (boolean update) {
		if (spawnDelay == null)
			return;

		timeTillCraft = spawnDelay.get(world.rand).intValue();
		lastTimeTillCraft = timeTillCraft;

		if (update)
			blockUpdate();
	}

	private double activationAmount () {
		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) {
			return 0;
		}

		double activationAmount = 0;

		List<String> entityTypes = new ArrayList<>();

		for (EntityLivingBase entity : world
			.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(activatingRange))) {

			if (!(entity instanceof EntityPlayer)) {
				String entityType = EntityList.getKey(entity).toString();
				if (entityTypes.contains(entityType))
					continue;
				entityTypes.add(entityType);

				activationAmount += 1;

				if (!world.isRemote && isConnected && hasValidRecipe() && getBlock().poofChance > world.rand
					.nextDouble()) {
					entity.setDead();
					mobPoofParticles(world, pos);
					mobPoofParticles(world, entity.getPosition());
				}
			}
		}

		return activationAmount;
	}

	public static void mobPoofParticles (World world, BlockPos pos) {
		if (world.isRemote) {
			particles(pos);
		} else {
			SoulsPacketHandler.INSTANCE.sendToAllAround(new MobPoof(pos), new TargetPoint(world.provider
				.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
		}
	}

	@SideOnly(Side.CLIENT)
	private static void particles (BlockPos pos) {
		World world = Minecraft.getMinecraft().world;
		Random rand = world.rand;

		for (int i = 0; i < ModBlocks.COMPOSER.particleCountMobPoof; ++i) {
			double d3 = (pos.getX() - 0.5F + rand.nextFloat());
			double d4 = (pos.getY() + rand.nextFloat());
			double d5 = (pos.getZ() - 0.5F + rand.nextFloat());
			double d3o = (d3 - pos.getX()) / 4;
			double d4o = (d4 - pos.getY()) / 5;
			double d5o = (d5 - pos.getZ()) / 4;
			ParticleManager
				.spawnParticle(world, ParticleType.MOB_POOF.getId(), false, d3 + 0.5F, d4, d5 + 0.5F, d3o, d4o, d5o, 1);
		}
	}

	@Override
	public void update () {
		double activationAmount = activationAmount();
		if (timeTillCraft > 0) {
			timeTillCraft -= activationAmount;
		}

		if (world.isRemote) {
			updateRenderer(activationAmount);

		} else {
			validateStructure();

			if (needsRecipeRefresh) {
				refreshRecipe();
			}

			if (isConnected && hasValidRecipe()) {
				if (timeTillCraft > 0) {
					int signalStrength = (int) Math.floor(16 * getCompositionPercent());
					if (signalStrength != this.signalStrength) {
						this.signalStrength = signalStrength;
						markDirty();
					}

					return;
				}

				if (!world.isRemote)
					completeCraft();
			}
		}
	}

	private Map<BlockPos, Byte> cellMap = new HashMap<>();

	public void validateStructure () {
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

			getBlock().structure
				.loopBlocks(world, pos, state.getValue(Composer.FACING), (BlockPos pos2, BlockValidator validator) -> {
					IBlockState currentState = world.getBlockState(pos2);

					if (currentState.getBlock() == ComposerCell.INSTANCE) {
						ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(pos2);
						BlockPos ccPos = ccte.getPos();
						world.setBlockState(ccPos, currentState
							.withProperty(ComposerCell.CELL_STATE, !isConnected ? ComposerCell.CellState.DISCONNECTED : ccPos
								.equals(center) ? ComposerCell.CellState.CONNECTED_CENTER : ComposerCell.CellState.CONNECTED_EDGE), 3);

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

	private BlockPos offset (BlockPos a, BlockPos b, double amt) {
		return a.add(b.getX() * amt, b.getY() * amt, b.getZ() * amt);
	}

	public float getCompositionPercent () {
		return (lastTimeTillCraft - timeTillCraft) / (float) lastTimeTillCraft;
	}

	public boolean loopComposerCells (ComposerCellHandler handler) {
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

		public Boolean handle (ComposerCellTileEntity te);
	}

	public void completeCraft () {
		if (!hasValidRecipe())
			return;

		ItemStack result = getStoredItem();
		dispenseItem(result.copy(), world, pos, world.getBlockState(pos).getValue(Composer.FACING));
		loopComposerCells(ccte -> {
			if (ccte.storedItem == null)
				return null;

			ccte.storedQuantity--;

			Item storedItem = ccte.storedItem.getItem();
			if (storedItem.hasContainerItem(ccte.storedItem)) {
				ItemStack containerItem = storedItem.getContainerItem(ccte.storedItem);
				dispenseItem(containerItem, world, ccte.getPos(), EnumFacing.DOWN);
			}

			if (ccte.storedQuantity <= 0) {
				ccte.storedQuantity = 0;
				ccte.storedItem = null;
				container.craftingMatrix.setInventorySlotContents(ccte.slot, ItemStack.EMPTY);
			}

			ccte.blockUpdate();

			return null;
		});

		blockUpdate();

		resetTimer();
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void onUpdateUpgrades (boolean readFromNBT) {

		Composer block = getBlock();

		int delayUpgrades = upgrades.get(Upgrade.DELAY);
		spawnDelay = new Range(block.nonUpgradedDelay.min / (1 + delayUpgrades * block.upgradeDelayEffectiveness.min), block.nonUpgradedDelay.max / (1 + delayUpgrades * block.upgradeDelayEffectiveness.max));

		int rangeUpgrades = upgrades.get(Upgrade.RANGE);
		activatingRange = block.nonUpgradedRange + rangeUpgrades * block.upgradeRangeEffectiveness;

		if (world != null && !world.isRemote) {
			if (!readFromNBT)
				resetTimer(false);
			blockUpdate();
		}
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {

		compound.setFloat("delay", timeTillCraft);
		compound.setFloat("delay_last", lastTimeTillCraft);

		NBTTagCompound cellTag = new NBTTagCompound();
		for (Map.Entry<BlockPos, Byte> cell : cellMap.entrySet()) {
			NBTTagCompound posTag = new NBTTagCompound();
			posTag.setInteger("x", cell.getKey().getX());
			posTag.setInteger("y", cell.getKey().getY());
			posTag.setInteger("z", cell.getKey().getZ());
			cellTag.setTag(cell.getValue().toString(), posTag);
		}
		compound.setTag("cell_map", cellTag);

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			compound.setTag("crafting_item", getStoredItem().writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {

		timeTillCraft = compound.getFloat("delay");
		lastTimeTillCraft = compound.getFloat("delay_last");

		NBTTagCompound cellTag = compound.getCompoundTag("cell_map");
		for (Integer slot = 0; slot < 9; slot++) {
			NBTTagCompound posTag = cellTag.getCompoundTag(slot.toString());
			cellMap.put(new BlockPos(posTag.getInteger("x"), posTag.getInteger("y"), posTag
				.getInteger("z")), (byte) (int) slot);
		}

		needsRecipeRefresh = true;

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			renderItem = new ItemStack(compound.getCompoundTag("crafting_item"));
		}
	}

	/////////////////////////////////////////
	// Renderer
	//

	private double itemRotation = 0;
	private double prevItemRotation = 0;
	private double timeTillParticle = 0;
	private ItemStack renderItem;

	@SideOnly(Side.CLIENT)
	private boolean isPlayerInRangeForEffects () {
		return world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 64);
	}

	@SideOnly(Side.CLIENT)
	public void updateRenderer (double activationAmount) {

		double diff = itemRotation - prevItemRotation;
		prevItemRotation = itemRotation;
		itemRotation += activationAmount <= 0 ? //
			diff * 0.9 // ease rotation to a stop
			: 1.0F * getCompositionPercent() + diff * 0.8; // normal rotation

		if (!hasValidRecipe() || !isPlayerInRangeForEffects() || activationAmount == 0)
			return;

		Composer composer = getBlock();
		if (composer.particleCountActivated < 1) {
			timeTillParticle += composer.particleCountActivated;

			if (timeTillParticle < 1)
				return;
		}

		timeTillParticle = 0;

		for (int i = 0; i < composer.particleCountActivated; i++) {
			double d3 = (pos.getX() + world.rand.nextFloat());
			double d4 = (pos.getY() + world.rand.nextFloat());
			double d5 = (pos.getZ() + world.rand.nextFloat());
			world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (d3 - pos.getX() - 0.5F), -0.3D, (d5 - pos
				.getZ() - 0.5F));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getItemRotation () {
		return itemRotation;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getPrevItemRotation () {
		return prevItemRotation;
	}

	@Override
	public ItemStack getStoredItem () {
		if (renderItem != null)
			return renderItem;
		if (container == null)
			return ItemStack.EMPTY;
		return container.craftResult.getStackInSlot(0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldComplexRotate () {
		return true;
	}

	/////////////////////////////////////////
	// Recipe
	//

	public static class ComposerContainer extends Container {

		public InventoryCrafting craftingMatrix;
		public InventoryCraftResult craftResult;
		private final World world;
		private final EntityPlayer player;
		private RecipeChangedHandler onRecipeChanged;

		public static class CraftingMatrix extends InventoryCrafting {

			public CraftingMatrix (ComposerContainer c, int width, int height) {
				super(c, width, height);
			}
		}

		public ComposerContainer (World world, EntityPlayer player) {
			this.world = world;
			this.player = player;
			this.craftingMatrix = new CraftingMatrix(this, 3, 3);
			this.craftResult = new InventoryCraftResult();
		}

		@Override
		public boolean canInteractWith (EntityPlayer playerIn) {
			return true;
		}

		@Override
		public void onCraftMatrixChanged (IInventory inventoryIn) {
			this.slotChangedCraftingGrid(this.world, this.player, this.craftingMatrix, this.craftResult);
		}

		@Override
		protected void slotChangedCraftingGrid (World world, EntityPlayer player, InventoryCrafting craftingMatrix, InventoryCraftResult craftResult) {

			if (!world.isRemote) {
				ItemStack stack = ItemStack.EMPTY;
				IRecipe recipe = CraftingManager.findMatchingRecipe(craftingMatrix, world);

				if (recipe != null) {
					craftResult.setRecipeUsed(recipe);
					stack = recipe.getCraftingResult(craftingMatrix);
					onRecipeChanged();
				}

				craftResult.setInventorySlotContents(0, stack);
			}
		}

		public void onRecipeChanged () {
			if (onRecipeChanged != null)
				onRecipeChanged.handle();
		}

		public void onRecipeChanged (RecipeChangedHandler handler) {
			onRecipeChanged = handler;
		}

		public static interface RecipeChangedHandler {

			public void handle ();
		}
	}

	private FakePlayer fakePlayer;
	private ComposerContainer container;
	private UUID uuid;
	private boolean needsRecipeRefresh = true;

	public void refreshRecipe () {
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

	public void refreshContainer () {
		uuid = UUID.randomUUID();
		fakePlayer = new FakePlayer((WorldServer) world, new GameProfile(uuid, "composer_tile_entity"));
		container = new ComposerContainer(world, fakePlayer);
		container.onRecipeChanged( () -> {
			resetTimer();
		});
	}

	public Boolean updateCCTEItem (ComposerCellTileEntity ccte, boolean blockUpdate) {
		ccte.onChangeItem(this::updateCCTEItem);
		container.craftingMatrix
			.setInventorySlotContents(ccte.slot, ccte.storedItem == null ? ItemStack.EMPTY : ccte.storedItem);

		if (blockUpdate)
			blockUpdate();

		return null;
	}

	public Boolean updateCCTEItem (ComposerCellTileEntity ccte) {
		return updateCCTEItem(ccte, true);
	}
}
