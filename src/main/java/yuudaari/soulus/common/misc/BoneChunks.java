package yuudaari.soulus.common.misc;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.item.ConfigBoneChunks;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.misc.ConfigFossils;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.misc.ConfigFossils.ConfigFossil;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.util.Range;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
@ConfigInjected(Soulus.MODID)
public class BoneChunks {

	/////////////////////////////////////////
	// Configs
	//

	@Inject public static ConfigBoneTypes CONFIG_BONE_TYPES;
	@Inject public static ConfigBoneChunks CONFIG;
	@Inject public static ConfigFossils CONFIG_FOSSILS;
	@Inject public static ConfigEssences CONFIG_ESSENCES;

	/////////////////////////////////////////
	// Basics
	//

	private static Map<String, Tuple2<Double, Map<String, Double>>> drops = new HashMap<>();

	public static double getChanceTotal (String boneType) {
		Tuple2<Double, Map<String, Double>> dropTuple = drops.get(boneType.toLowerCase());
		return dropTuple == null ? 0 : dropTuple._1;
	}

	public static void registerEssenceDrops (FMLStateEvent e) {
		for (ConfigBoneType boneType : CONFIG_BONE_TYPES.boneTypes) {
			double chanceTotal = 0;
			Map<String, Double> drops = new HashMap<>();

			for (ConfigEssence essenceConfig : CONFIG_ESSENCES.essences) {
				if (essenceConfig.bones == null || !essenceConfig.bones.type.equalsIgnoreCase(boneType.name)) {
					continue;
				}

				if (essenceConfig.essence.equals("NONE")) {
					drops.put(null, essenceConfig.bones.dropWeight);
				} else {
					if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(essenceConfig.essence))) {
						drops.put(essenceConfig.essence, essenceConfig.bones.dropWeight);
					} else {
						System.out.println(String
							.format("Colour entry missing for %s:%s", boneType, essenceConfig.essence));
					}
				}
			}

			for (double dropChance : drops.values()) {
				chanceTotal += dropChance;
			}

			BoneChunks.drops.put(boneType.name.toLowerCase(), new Tuple2<>(chanceTotal, drops));
		}
	}

	/////////////////////////////////////////
	// Events
	//

	@SubscribeEvent
	public static void onRightClick (RightClickItem event) {
		ItemStack heldItem = event.getItemStack();
		String itemName = heldItem.getItem().getRegistryName().toString();

		for (ConfigBoneType boneType : CONFIG_BONE_TYPES.boneTypes) {
			if (boneType.item_chunk.equalsIgnoreCase(itemName)) {

				World world = event.getWorld();
				EntityPlayer player = event.getEntityPlayer();

				if (!world.isRemote) {
					List<ItemStack> drops = getDrops(boneType.name);
					for (ItemStack drop : drops) {
						EntityItem dropEntity = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
						dropEntity.setNoPickupDelay();
						world.spawnEntity(dropEntity);
					}
				}
				heldItem.setCount(heldItem.getCount() - 1);

				particles(world, player, heldItem.getItem());
			}
		}
	}

	@SubscribeEvent
	public static void onHarvest (HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			String blockId = event.getState().getBlock().getRegistryName().toString();
			ConfigFossil fossilConfig = CONFIG_FOSSILS.get(blockId);

			if (fossilConfig != null) {
				List<ItemStack> drops = event.getDrops();
				drops.clear();

				int count = new Range(fossilConfig.min * (1 + event
					.getFortuneLevel() / 3), fossilConfig.max * (1 + event.getFortuneLevel() / 3))
						.getInt(event.getWorld().rand);
				drops.add(CONFIG_BONE_TYPES.get(fossilConfig.type).getChunkStack(count));
			}
		}
	}

	/////////////////////////////////////////
	// Util
	//

	private static List<ItemStack> getDrops (String boneType) {
		List<ItemStack> result = new ArrayList<>();
		Random rand = new Random();

		Tuple2<Double, Map<String, Double>> dropMap = BoneChunks.drops.get(boneType);
		double chanceTotal = dropMap._1;
		Map<String, Double> drops = dropMap._2;

		for (Map.Entry<String, Double> dropInfo : drops.entrySet()) {
			if (dropInfo.getKey() == null) continue;

			if (rand.nextFloat() < dropInfo.getValue() / chanceTotal) {
				result.add(Essence.getStack(dropInfo.getKey()));
			}
		}

		return result;
	}

	private static void particles (World world, EntityPlayer player, Item item) {
		for (int i = 0; i < CONFIG.particleCount; ++i) {
			Vec3d v = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			v = v.rotatePitch(-player.rotationPitch * 0.017453292F);
			v = v.rotateYaw(-player.rotationYaw * 0.017453292F);
			double d0 = (double) (-world.rand.nextFloat()) * 0.6D - 0.3D;
			Vec3d v2 = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			v2 = v2.rotatePitch(-player.rotationPitch * 0.017453292F);
			v2 = v2.rotateYaw(-player.rotationYaw * 0.017453292F);
			v2 = v2.addVector(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);

			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, v2.x, v2.y, v2.z, v.x, v.y + 0.05D, v.z, Item
				.getIdFromItem(item));
		}

		player.playSound(SoundEvents.BLOCK_GRAVEL_HIT, 0.5F + 0.5F * (float) world.rand
			.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
	}

}
