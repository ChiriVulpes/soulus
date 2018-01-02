package yuudaari.soulus.common.block.skewer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuudaari.soulus.common.block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.block.UpgradeableBlock.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.block.skewer.Skewer.Upgrade;
import yuudaari.soulus.common.item.BloodCrystal;
import yuudaari.soulus.common.misc.ModDamageSource;
import yuudaari.soulus.common.util.Logger;

public class SkewerTileEntity extends UpgradeableBlockTileEntity {

	public int bloodCrystalBlood = 0;

	private Map<EntityLivingBase, Long> entityHitTimes = new HashMap<>();

	@Override
	public Skewer getBlock() {
		return Skewer.INSTANCE;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void update() {
		if (world.isRemote)
			return;

		Skewer skewer = getBlock();
		IBlockState state = world.getBlockState(pos);
		if (state.getValue(Skewer.EXTENDED)) {
			EnumFacing facing = state.getValue(Skewer.FACING);
			long time = world.getTotalWorldTime();

			entityHitTimes.entrySet()
					.removeIf(entityHitTime -> time - entityHitTime.getValue() > getBlock().ticksBetweenDamage);

			for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class,
					Skewer.getSpikeHitbox(facing, pos))) {

				if (!entity.getIsInvulnerable() && !entityHitTimes.containsKey(entity)) {

					entityHitTimes.put(entity, world.getTotalWorldTime());

					float damage = skewer.baseDamage;

					damage += skewer.upgradeDamageEffectiveness * upgrades.get(Upgrade.DAMAGE);

					int rtime = entity.hurtResistantTime;
					entity.attackEntityFrom(ModDamageSource.SKEWER, damage);
					entity.hurtResistantTime = rtime;

					if (world.rand.nextDouble() < skewer.chanceForBloodPerHit
							&& upgrades.get(Upgrade.BLOOD_CRYSTAL) == 1) {
						bloodCrystalBlood += getBlock().bloodPerDamage * damage;
						if (bloodCrystalBlood > BloodCrystal.INSTANCE.requiredBlood) {
							bloodCrystalBlood = BloodCrystal.INSTANCE.requiredBlood;
						}
						BloodCrystal.bloodParticles(entity);
						blockUpdate();
					}
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onInsertUpgrade(ItemStack stack, IUpgrade upgrade, int newQuantity) {
		if (upgrade == Upgrade.BLOOD_CRYSTAL) {
			this.bloodCrystalBlood = BloodCrystal.getContainedBlood(stack);
		}
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onReadFromNBT(NBTTagCompound compound) {
		bloodCrystalBlood = compound.getInteger("blood_crystal_stored_blood");
	}

	@Override
	public void onWriteToNBT(NBTTagCompound compound) {
		compound.setInteger("blood_crystal_stored_blood", bloodCrystalBlood);
	}

}