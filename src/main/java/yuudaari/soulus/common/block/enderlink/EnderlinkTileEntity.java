package yuudaari.soulus.common.block.enderlink;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.enderlink.Enderlink.Upgrade;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigEnderlink;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.Soulus;

@ConfigInjected(Soulus.MODID)
public class EnderlinkTileEntity extends UpgradeableBlockTileEntity implements ITickable {

	/////////////////////////////////////////
	// Main
	//

	@Inject public static ConfigEnderlink CONFIG;

	@Override
	public Enderlink getBlock () {
		return ModBlocks.ENDERLINK;
	}

	private int range;
	public EnumDyeColor color = EnumDyeColor.LIGHT_BLUE;

	public boolean setColor (EnumDyeColor color) {
		if (this.color == color) return false;

		this.color = color;
		blockUpdate();
		return true;
	}

	public void teleportEntity (Entity entity) {
		EnumFacing facing = world.getBlockState(pos).getValue(Enderlink.FACING);
		BlockPos teleportPos = pos.offset(facing);

		boolean isItem = entity instanceof EntityItem;
		int particleCount = isItem ? CONFIG.particleCountTeleportItem : CONFIG.particleCountTeleport;
		explosionParticles(entity, particleCount);

		if (isItem) {
			ItemStack item = ((EntityItem) entity).getItem();
			IInventory facingInventory = getFacingInventory(world, pos, facing);
			if (facingInventory != null) {
				insertItem(item, world, pos, facing);
				return;
			}
		}

		double x = teleportPos.getX() + 0.5 - facing.getFrontOffsetX() / 2.0,
			y = teleportPos.getY() + 0.5 - facing.getFrontOffsetY() / 2.0,
			z = teleportPos.getZ() + 0.5 - facing.getFrontOffsetZ() / 2.0;

		if (facing == EnumFacing.DOWN) {
			y -= entity.height;
		} else if (facing != EnumFacing.UP) {
			x += facing.getFrontOffsetX() * entity.width / 2;
			y += facing.getFrontOffsetY() * entity.width / 2;
			z += facing.getFrontOffsetZ() * entity.width / 2;
		}

		entity.setPositionAndUpdate(x, y, z);
		explosionParticles(entity, particleCount);
	}

	public boolean isWithinRange (Entity entity) {
		return entity.getDistanceSqToCenter(pos) < range;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void update () {

	}

	@Override
	public void onUpdateUpgrades (boolean readFromNBT) {

		int rangeUpgrades = upgrades.get(Upgrade.RANGE);
		range = (int) Math.pow(CONFIG.nonUpgradedRange + rangeUpgrades * CONFIG.upgradeRangeEffectiveness, 2);

		if (world != null && !world.isRemote) {
			blockUpdate();
		}

	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		color = EnumDyeColor.byMetadata(compound.getInteger("color"));

		onUpdateUpgrades(false);
	}

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		compound.setInteger("color", color.getMetadata());
	}

	/////////////////////////////////////////
	// Render
	//

	private void explosionParticles (Entity entity, int particleCount) {
		Random rand = world.rand;

		WorldServer worldServer = world.getMinecraftServer().getWorld(entity.dimension);

		for (int i = 0; i < particleCount; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			double w = entity.width, h = entity.height;
			double x = entity.posX + (double) (rand.nextFloat() * w * 2.0F) - (double) w - d0 * 10.0D,
				y = entity.posY + (double) (rand.nextFloat() * h) - d1 * 10.0D,
				z = entity.posZ + (double) (rand.nextFloat() * w * 2.0F) - (double) w - d2 * 10.0D;
			double s = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
			worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH, x, y, z, 1, d0, d1, d2, s);
		}
	}

}
