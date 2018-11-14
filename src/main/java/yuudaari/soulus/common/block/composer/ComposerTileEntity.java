package yuudaari.soulus.common.block.composer;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.client.util.ParticleType;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.block.composer.Composer.Upgrade;
import yuudaari.soulus.common.block.composer.ComposerCell.CellState;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigComposer;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.MobPoof;
import yuudaari.soulus.common.recipe.IRecipeComposer;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.StructureMap.BlockValidator;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

@ConfigInjected(Soulus.MODID)
public class ComposerTileEntity extends HasRenderItemTileEntity {

	private boolean isConnected = false;
	private float timeTillCraft = 5;
	private float lastTimeTillCraft = 10;
	private int activatingRange;
	private Range spawnDelay;
	private int signalStrength;
	private double activationAmount = 0;
	private float poofChance = 0;
	private @Nullable UUID owner;

	@Override
	public Composer getBlock () {
		return ModBlocks.COMPOSER;
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
		return signalStrength;
	}

	public double getActivationAmount () {
		return activationAmount;
	}

	public double getPoofChance () {
		return poofChance;
	}

	public void setOwner (EntityPlayer owner) {
		this.owner = owner.getUniqueID();
	}

	public @Nullable EntityPlayer getOwner () {
		return owner == null ? null : world.getPlayerEntityByUUID(owner);
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject public static ConfigComposer CONFIG;

	/////////////////////////////////////////
	// Update
	//

	private void resetTimer () {
		resetTimer(true);
	}

	private void resetTimer (boolean update) {
		if (spawnDelay == null)
			return;

		timeTillCraft = spawnDelay.getInt(world.rand) * (this.container == null ? 1 : this.container.time);
		lastTimeTillCraft = timeTillCraft;

		if (update)
			blockUpdate();
	}

	public void updateActivationAmount () {
		activationAmount = 0;

		if (!isConnected) return;

		// when powered by redstone, don't run
		if (world.isBlockIndirectlyGettingPowered(pos) != 0) return;

		if (!hasValidRecipe()) return;

		final double poofChance = CONFIG.poofChance.get( //
			upgrades.get(Upgrade.EFFICIENCY) / (double) Upgrade.EFFICIENCY.getMaxQuantity());

		// the chance of poofing increases over time
		this.poofChance += poofChance;

		final List<String> entityTypes = new ArrayList<>();

		final EnumFacing facing = world.getBlockState(pos).getValue(Composer.FACING).getOpposite();
		final AxisAlignedBB activationBox = new AxisAlignedBB(pos.offset(facing, 2)).grow(activatingRange);

		for (final EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, activationBox)) {

			final List<String> whitelist = CONFIG.whitelistedCreatures;
			final List<String> blacklist = CONFIG.blacklistedCreatures;
			final boolean whitelistAll = whitelist == null ? false : whitelist.contains("*");
			final boolean blacklistAll = blacklist == null ? false : blacklist.contains("*");

			if (!(entity instanceof EntityPlayer)) {
				final ResourceLocation entityType = EntityList.getKey(entity);
				if (entityTypes.contains(entityType.toString()))
					continue;

				final int whitelistLevel = (whitelistAll ? 1 : 0) + //
					(whitelist != null && whitelist.contains(entityType.getResourceDomain() + ":*") ? 2 : 0) + //
					(whitelist != null && whitelist.contains(entityType.toString()) ? 4 : 0) - //
					(blacklistAll ? 1 : 0) - //
					(blacklist != null && blacklist.contains(entityType.getResourceDomain() + ":*") ? 2 : 0) - //
					(blacklist != null && blacklist.contains(entityType.toString()) ? 4 : 0);

				if (whitelistLevel < 0)
					continue;

				entityTypes.add(entityType.toString());

				activationAmount += 1;

				if (!world.isRemote && isConnected && hasValidRecipe() && this.poofChance > world.rand.nextDouble()) {
					// reset poof chance
					this.poofChance = 0;
					// blockUpdate(); (only needed if chance is rendered)

					entity.setDead();
					mobPoofParticles(world, pos);
					mobPoofParticles(world, entity.getPosition());
				}
			}
		}
	}

	private int timeTillNextMajorUpdate = 0;

	@Override
	public void update () {

		if (timeTillNextMajorUpdate-- < 0) {
			timeTillNextMajorUpdate = 20;
			validateStructure();
			updateActivationAmount();
		}

		if (world.isRemote) {
			updateRenderer(activationAmount);

		} else {
			if (needsRecipeRefresh) refreshRecipe();
			updateSignalStrength(activationAmount);
		}

		timeTillCraft -= activationAmount;

		if (timeTillCraft <= 0) {
			if (!world.isRemote) {
				completeCraft();
			}

			resetTimer();
		}
	}

	private void updateSignalStrength (double activationAmount) {
		int signalStrength = activationAmount > 0 ? //
			(int) Math.floor(15 * getCompositionPercent()) + 1 : 0;
		if (signalStrength != this.signalStrength) {
			this.signalStrength = signalStrength;
			markDirty();
		}
	}

	private Map<BlockPos, Byte> cellMap = new HashMap<>();

	public void validateStructure () {
		IBlockState state = world.getBlockState(pos);
		EnumFacing currentDirection = state.getValue(Composer.FACING);

		EnumFacing direction = getBlock().validateStructure(world, pos, currentDirection);

		if (isConnected != (direction != null)) {
			isConnected = direction != null;
			needsRecipeRefresh = true;
		}

		boolean changedState = false;

		if (isConnected && currentDirection != direction) {
			state = state.withProperty(Composer.FACING, direction);
			currentDirection = direction;
			changedState = true;
		}

		if (state.getValue(Composer.CONNECTED) != isConnected) {
			state = state.withProperty(Composer.CONNECTED, isConnected);
			changedState = true;

			if (isConnected) {
				connectCells(direction, state);
			} else {
				disconnectCells(currentDirection);
			}

			needsRecipeRefresh = true;
		}

		if (changedState)
			world.setBlockState(pos, state, 3);
	}

	private void disconnectCells (EnumFacing direction) {
		getBlock().structure.loopBlocks(world, pos, direction, (BlockPos cellPos, BlockValidator validator) -> {
			IBlockState cellState = world.getBlockState(cellPos);

			if (cellState.getBlock() == ModBlocks.COMPOSER_CELL) {
				ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(cellPos);
				cellState = cellState.withProperty(ComposerCell.CELL_STATE, CellState.DISCONNECTED);
				world.setBlockState(cellPos, cellState, 3);

				ccte.composerLocation = null;
				ccte.changeComposerCooldown = 20;
				ccte.slot = -1;
				ccte.blockUpdate();
				ccte.onChangeItem(null);
			}

			return null;
		});

		cellMap.clear();
	}

	private void connectCells (EnumFacing direction, IBlockState state) {
		BlockPos center = direction == null ? null : pos.offset(direction, -3);
		BlockPos x = direction == null ? null : BlockPos.ORIGIN.offset(direction, 1);
		BlockPos z = direction == null ? null : BlockPos.ORIGIN.offset(direction.rotateY(), 1);
		BlockPos topLeft = direction == null ? null : offset(offset(pos, x, -4), z, -1);

		cellMap.clear();
		for (int iz = 0; iz < 3; iz++) {
			for (int ix = 0; ix < 3; ix++) {
				BlockPos cellPos = offset(offset(topLeft, x, ix), z, iz);
				cellMap.put(cellPos, (byte) (ix + iz * 3));
			}
		}

		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) return;

		getBlock().structure.loopBlocks(world, pos, direction, (BlockPos cellPos, BlockValidator validator) -> {
			IBlockState cellBlockState = world.getBlockState(cellPos);

			if (cellBlockState.getBlock() == ModBlocks.COMPOSER_CELL) {
				ComposerCellTileEntity ccte = (ComposerCellTileEntity) world.getTileEntity(cellPos);
				CellState cellState = cellPos.equals(center) ? CellState.CONNECTED_CENTER : CellState.CONNECTED_EDGE;
				world.setBlockState(cellPos, cellBlockState.withProperty(ComposerCell.CELL_STATE, cellState), 3);

				ccte.composerLocation = pos;
				ccte.changeComposerCooldown = 20;
				ccte.slot = cellMap.get(cellPos);
				ccte.blockUpdate();
				ccte.onChangeItem(this::updateCCTEItem);
			}

			return null;
		});
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

		if (!loopComposerCells(ccte -> null)) {
			validateStructure();
			if (!isConnected) return;
		}

		ItemStack result = getStoredItem();
		dispenseItem(result.copy(), world, pos, world.getBlockState(pos).getValue(Composer.FACING));

		Advancements.COMPOSE.trigger(getOwner(), this.container.lastRecipe.getRegistryName().toString());

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
				if (container == null || container.craftingMatrix == null) {
					refreshContainer();
				}
				container.craftingMatrix.setInventorySlotContents(ccte.slot, ItemStack.EMPTY);
			}

			ccte.blockUpdate();

			return null;
		});
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void onUpdateUpgrades (boolean readFromNBT) {

		int delayUpgrades = upgrades.get(Upgrade.DELAY);
		spawnDelay = new Range(CONFIG.nonUpgradedDelay.min / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.min), CONFIG.nonUpgradedDelay.max / (1 + delayUpgrades * CONFIG.upgradeDelayEffectiveness.max));

		int rangeUpgrades = upgrades.get(Upgrade.RANGE);
		activatingRange = CONFIG.nonUpgradedRange + rangeUpgrades * CONFIG.upgradeRangeEffectiveness;

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

		compound.setBoolean("connected", isConnected);
		compound.setFloat("delay", timeTillCraft);
		compound.setFloat("delay_last", lastTimeTillCraft);
		compound.setFloat("poof_chance", poofChance);

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

		isConnected = compound.getBoolean("connected");
		timeTillCraft = compound.getFloat("delay");
		lastTimeTillCraft = compound.getFloat("delay_last");
		poofChance = compound.getFloat("poof_chance");

		NBTTagCompound cellTag = compound.getCompoundTag("cell_map");
		for (Integer slot = 0; slot < 9; slot++) {
			NBTTagCompound posTag = cellTag.getCompoundTag(slot.toString());
			cellMap.put(new BlockPos(posTag.getInteger("x"), posTag.getInteger("y"), posTag
				.getInteger("z")), (byte) (int) slot);
		}

		needsRecipeRefresh = true;
		isInitialRecipeRefresh = true;

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
		double particleCount = CONFIG.particleCountActivated * Math
			.max(1, Math.min(CONFIG.particleCountMax, activationAmount)) * (0.5 + getCompositionPercent() / 2);
		if (particleCount < 1) {
			timeTillParticle += 0.01 + particleCount;

			if (timeTillParticle < 1)
				return;
		}

		timeTillParticle = 0;

		for (int i = 0; i < CONFIG.particleCountActivated; i++) {
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

		for (int i = 0; i < CONFIG.particleCountMobPoof; ++i) {
			double d3 = (pos.getX() - 0.5F + rand.nextFloat());
			double d4 = (pos.getY() + rand.nextFloat());
			double d5 = (pos.getZ() - 0.5F + rand.nextFloat());
			double d3o = (d3 - pos.getX()) / 4;
			double d4o = (d4 - pos.getY()) / 5;
			double d5o = (d5 - pos.getZ()) / 4;
			world.spawnParticle(ParticleType.MOB_POOF.getId(), false, d3 + 0.5F, d4, d5 + 0.5F, d3o, d4o, d5o, 1);
		}
	}

	/////////////////////////////////////////
	// Recipe
	//

	public static class ComposerContainer extends Container {

		public InventoryCrafting craftingMatrix;
		public InventoryCraftResult craftResult;
		public float time = 1;
		private final World world;
		private final EntityPlayer player;
		private RecipeChangedHandler recipeChangedHandler;
		private IRecipe lastRecipe;

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

				if (recipe != null && recipe != lastRecipe) {
					lastRecipe = recipe;
					craftResult.setRecipeUsed(recipe);

					time = 1;

					if (recipe instanceof IRecipeComposer)
						time = ((IRecipeComposer) recipe).getTime();

					stack = recipe.getCraftingResult(craftingMatrix);

					if (recipeChangedHandler != null)
						recipeChangedHandler.handle();
				}

				craftResult.setInventorySlotContents(0, stack);
			}
		}

		public void onRecipeChanged (RecipeChangedHandler handler) {
			recipeChangedHandler = handler;
		}

		public static interface RecipeChangedHandler {

			public void handle ();
		}
	}

	private UUID uuid;
	private FakePlayer fakePlayer;
	private ComposerContainer container;
	private boolean needsRecipeRefresh = true;
	private boolean isInitialRecipeRefresh = false;

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

		isInitialRecipeRefresh = false;

		blockUpdate();
	}

	public void refreshContainer () {
		uuid = UUID.randomUUID();
		fakePlayer = new FakePlayer((WorldServer) world, new GameProfile(uuid, "composer_tile_entity"));
		container = new ComposerContainer(world, fakePlayer);
		container.onRecipeChanged( () -> {
			if (!isInitialRecipeRefresh) resetTimer();
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
