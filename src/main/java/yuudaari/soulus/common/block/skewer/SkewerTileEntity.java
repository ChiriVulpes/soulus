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
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.block.ConfigSkewer;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.skewer.Skewer.Upgrade;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.misc.ModDamageSource;

@ConfigInjected(Soulus.MODID)
public class SkewerTileEntity extends UpgradeableBlockTileEntity {

	public int crystalBloodContainedBlood = 0;

	private Map<EntityLivingBase, Long> entityHitTimes = new HashMap<>();

	@Override
	public Skewer getBlock () {
		return ModBlocks.SKEWER;
	}

	/////////////////////////////////////////
	// Config
	//

	@Inject(ConfigSkewer.class) public static ConfigSkewer CONFIG;

	/////////////////////////////////////////
	// Events
	//

	@Override
	public void update () {
		if (world.isRemote)
			return;

		IBlockState state = world.getBlockState(pos);
		if (state.getValue(Skewer.EXTENDED)) {
			EnumFacing facing = state.getValue(Skewer.FACING);
			long time = world.getTotalWorldTime();

			entityHitTimes.entrySet()
				.removeIf(entityHitTime -> time - entityHitTime.getValue() > CONFIG.ticksBetweenDamage);

			for (EntityLivingBase entity : world
				.getEntitiesWithinAABB(EntityLivingBase.class, Skewer.getSpikeHitbox(facing, pos))) {

				if (!entity.getIsInvulnerable() && !entityHitTimes.containsKey(entity)) {

					entityHitTimes.put(entity, world.getTotalWorldTime());

					float damage = CONFIG.baseDamage;

					damage += CONFIG.upgradeDamageEffectiveness * upgrades.get(Upgrade.DAMAGE);

					int rtime = entity.hurtResistantTime;
					entity.attackEntityFrom(ModDamageSource.SKEWER, damage);
					entity.hurtResistantTime = rtime;

					if (world.rand.nextDouble() < CONFIG.chanceForBloodPerHit && upgrades
						.get(Upgrade.CRYSTAL_BLOOD) == 1) {
						crystalBloodContainedBlood += CONFIG.bloodPerDamage * damage;
						if (crystalBloodContainedBlood > CrystalBlood.CONFIG.requiredBlood) {
							crystalBloodContainedBlood = CrystalBlood.CONFIG.requiredBlood;
						}
						CrystalBlood.bloodParticles(entity);
						blockUpdate();
					}
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onInsertUpgrade (ItemStack stack, IUpgrade upgrade, int newQuantity) {
		if (upgrade == Upgrade.CRYSTAL_BLOOD) {
			this.crystalBloodContainedBlood = CrystalBlood.getContainedBlood(stack);

		} else if (upgrades.get(Upgrade.CRYSTAL_BLOOD) > 0) {
			// always keep blood crystal at the top (first to remove)
			this.insertionOrder.remove(Upgrade.CRYSTAL_BLOOD);
			this.insertionOrder.push(Upgrade.CRYSTAL_BLOOD);
		}
	}

	/////////////////////////////////////////
	// NBT
	//

	@Override
	public void onReadFromNBT (NBTTagCompound compound) {
		crystalBloodContainedBlood = compound.getInteger("crystal_blood_stored_blood");
	}

	@Override
	public void onWriteToNBT (NBTTagCompound compound) {
		compound.setInteger("crystal_blood_stored_blood", crystalBloodContainedBlood);
	}

}
