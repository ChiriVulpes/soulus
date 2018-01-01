package yuudaari.soulus.common.block.skewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.UpgradeableBlock;
import yuudaari.soulus.common.block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.block.UpgradeableBlock.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.block.skewer.Skewer.Upgrade;
import yuudaari.soulus.common.misc.ModDamageSource;

public class SkewerTileEntity extends UpgradeableBlockTileEntity {

	@Override
	public UpgradeableBlock getBlock() {
		return Skewer.INSTANCE;
	}

	@Override
	public void update() {
		EnumFacing facing = world.getBlockState(pos).getValue(Skewer.FACING);
		for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class,
				Skewer.getSpikeHitbox(facing, pos))) {
			entity.attackEntityFrom(ModDamageSource.SKEWER, 1);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onReadFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	@Override
	public void onWriteToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaTooltip(List<String> currenttip, boolean isSneaking) {
		currenttip.add(I18n.format("waila." + Soulus.MODID
				+ (world.getBlockState(pos).getValue(Skewer.EXTENDED) ? ":skewer.extended" : ":skewer.not_extended")));

		if (isSneaking) {
			List<IUpgrade> upgrades = new ArrayList<>(Arrays.asList(Upgrade.values()));
			for (IUpgrade upgrade : Lists.reverse(insertionOrder)) {
				upgrades.remove(upgrade);
				currenttip.add(I18n
						.format("waila." + Soulus.MODID + ":skewer.has_upgrade_" + upgrade.getName().toLowerCase()));
			}
			for (IUpgrade upgrade : upgrades) {
				currenttip.add(I18n.format(
						"waila." + Soulus.MODID + ":skewer.missing_upgrade_" + upgrade.getName().toLowerCase()));
			}
		} else {
			currenttip.add(I18n.format("waila." + Soulus.MODID + ":skewer.show_upgrades"));
		}

		return currenttip;
	}
}