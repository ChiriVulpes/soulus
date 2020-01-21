package yuudaari.soulus.common.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.Tuple2;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.bones.ConfigBoneType;
import yuudaari.soulus.common.config.bones.ConfigBoneTypes;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.item.ConfigBoneChunks;
import yuudaari.soulus.common.config.misc.ConfigFossils;
import yuudaari.soulus.common.config.misc.ConfigFossils.ConfigFossil;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.XP;

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
	public static void onRightClick (final RightClickItem event) {
		final ItemStack heldItem = event.getItemStack();
		final String itemName = heldItem.getItem().getRegistryName().toString();

		for (final ConfigBoneType boneType : CONFIG_BONE_TYPES.boneTypes) {
			if (boneType.itemChunk.equalsIgnoreCase(itemName)) {

				final World world = event.getWorld();
				final EntityPlayer player = event.getEntityPlayer();

				particles(world, player, heldItem.getItem());

				final int count = player.isSneaking() && CONFIG.sneakToMarrowFullStack ? heldItem.getCount() : 1;

				if (!world.isRemote) {
					final Collection<ItemStack> drops = getMarrowingDrops(world.rand, boneType.name, count);
					for (final ItemStack drop : drops) {
						final EntityItem dropEntity = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
						dropEntity.setNoPickupDelay();
						world.spawnEntity(dropEntity);
					}

					for (int i = 0; i < count; i++)
						XP.grant(player, CONFIG.xp.getInt(world.rand));
				}

				heldItem.shrink(count);
			}
		}
	}

	@SubscribeEvent
	public static void onHarvest (final BreakEvent event) {
		if (event.getPlayer() == null)
			return;

		String blockId = event.getState().getBlock().getRegistryName().toString();
		ConfigFossil fossilConfig = CONFIG_FOSSILS.get(blockId);

		if (fossilConfig != null)
			event.setExpToDrop(fossilConfig.xp.getInt(event.getWorld().rand));
	}

	@SubscribeEvent
	public static void onHarvest (final HarvestDropsEvent event) {
		if (event.getHarvester() == null)
			return;

		String blockId = event.getState().getBlock().getRegistryName().toString();
		ConfigFossil fossilConfig = CONFIG_FOSSILS.get(blockId);

		if (fossilConfig == null)
			return;

		List<ItemStack> drops = event.getDrops();
		drops.clear();

		final ConfigBoneType boneType = CONFIG_BONE_TYPES.get(fossilConfig.type);
		if (boneType == null) {
			Logger.warn("No bone type registered for fossil '" + fossilConfig.type + "'");
			return;
		}

		final int amt = (int) (fossilConfig.chunks.get(event.getWorld().rand) * (1.0 + event.getFortuneLevel() / 3.0));
		drops.add(boneType.getChunkStack(amt));

		if (event.getWorld().rand.nextDouble() < fossilConfig.fullBoneChance)
			drops.add(boneType.getBoneStack());
	}

	/////////////////////////////////////////
	// Util
	//

	public static Collection<ItemStack> getMarrowingDrops (final Random rand, final String boneType, final int count) {
		final Map<String, ItemStack> result = new HashMap<>();

		final Tuple2<Double, Map<String, Double>> dropMap = BoneChunks.drops.get(boneType.toLowerCase());
		final double chanceTotal = dropMap._1;
		final Map<String, Double> drops = dropMap._2;

		for (int i = 0; i < count; i++) {
			for (final Map.Entry<String, Double> dropInfo : drops.entrySet()) {
				if (dropInfo.getKey() == null) continue;

				if (rand.nextFloat() >= dropInfo.getValue() / chanceTotal) continue;

				ItemStack stack = result.get(dropInfo.getKey());
				if (stack == null) {
					stack = Essence.getStack(dropInfo.getKey());
					result.put(dropInfo.getKey(), stack);
				} else
					stack.grow(1);

			}
		}

		return result.values();
	}

	public static void particles (final World world, final EntityPlayer player, final Item item) {
		for (int i = 0; i < CONFIG.particleCount; ++i) {
			Vec3d v = new Vec3d(((double) world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			v = v.rotatePitch(-player.rotationPitch * 0.017453292F);
			v = v.rotateYaw(-player.rotationYaw * 0.017453292F);
			final double d0 = (double) (-world.rand.nextFloat()) * 0.6D - 0.3D;
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
