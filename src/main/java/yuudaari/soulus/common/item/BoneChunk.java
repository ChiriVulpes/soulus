package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.CreatureConfig;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.ModItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BoneChunk extends ModItem {

	private Map<String, Double> drops = new HashMap<>();
	private int chanceTotal = 0;

	public BoneChunk(String name, BoneType boneType) {
		super(name);

		addOreDict("boneChunk");

		Soulus.onInit((FMLInitializationEvent e) -> {
			for (CreatureConfig creatureConfig : Soulus.config.creatures) {
				if (creatureConfig.bones.type != boneType) {
					continue;
				}

				if (creatureConfig.essence.equals("NONE")) {
					drops.put(null, creatureConfig.bones.dropWeight);
				} else {
					if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(creatureConfig.essence))) {
						drops.put(creatureConfig.essence, creatureConfig.bones.dropWeight);
					} else {
						System.out.println(String.format("Colour entry missing for %s:%s", boneType.name(),
								creatureConfig.essence));
					}
				}
			}

			for (double dropChance : drops.values()) {
				chanceTotal += dropChance;
			}
		});
	}

	@Nullable
	private ItemStack getDrop() {
		int choice = new Random().nextInt(chanceTotal);
		for (Map.Entry<String, Double> dropInfo : drops.entrySet()) {
			choice -= dropInfo.getValue();
			if (choice < 0) {
				String drop = dropInfo.getKey();
				if (drop != null) {
					return ModItems.ESSENCE.getStack(drop);
				}
				return null;
			}
		}
		throw new RuntimeException("Bonechunk drop failed!");
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!world.isRemote) {
			ItemStack drop = getDrop();
			if (drop != null) {
				EntityItem dropEntity = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
				dropEntity.setNoPickupDelay();
				world.spawnEntity(dropEntity);
			}
		}
		heldItem.setCount(heldItem.getCount() - 1);

		particles(world, player);

		return new ActionResult<>(EnumActionResult.PASS, heldItem);
	}

	private void particles(World world, EntityPlayer player) {
		for (int i = 0; i < Soulus.config.boneChunkParticleCount; ++i) {
			Vec3d v = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			v = v.rotatePitch(-player.rotationPitch * 0.017453292F);
			v = v.rotateYaw(-player.rotationYaw * 0.017453292F);
			double d0 = (double) (-world.rand.nextFloat()) * 0.6D - 0.3D;
			Vec3d v2 = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			v2 = v2.rotatePitch(-player.rotationPitch * 0.017453292F);
			v2 = v2.rotateYaw(-player.rotationYaw * 0.017453292F);
			v2 = v2.addVector(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);

			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, v2.x, v2.y, v2.z, v.x, v.y + 0.05D, v.z,
					Item.getIdFromItem(this));
		}

		player.playSound(SoundEvents.BLOCK_GRAVEL_HIT, 0.5F + 0.5F * (float) world.rand.nextInt(2),
				(world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
	}
}