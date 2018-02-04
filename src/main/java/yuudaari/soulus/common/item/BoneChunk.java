package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigBoneChunks;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.misc.ConfigFossils;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.misc.ConfigFossils.ConfigFossil;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.common.util.Range;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
@ConfigInjected(Soulus.MODID)
public class BoneChunk extends ModItem {

	/////////////////////////////////////////
	// Configs
	//

	@Inject public static ConfigBoneChunks CONFIG;
	@Inject public static ConfigFossils CONFIG_FOSSILS;
	@Inject public static ConfigEssences CONFIG_ESSENCES;

	/////////////////////////////////////////
	// Basics
	//

	public static Map<BoneType, BoneChunk> boneChunkTypes = new HashMap<>();

	private Map<String, Double> drops = new HashMap<>();
	private double chanceTotal = 0;
	private BoneType boneType;

	public double getChanceTotal () {
		return chanceTotal;
	}

	public BoneChunk (String name, BoneType boneType) {
		super(name);

		this.boneType = boneType;
		boneChunkTypes.put(boneType, this);

		addOreDict("boneChunk");

		Soulus.onPostInit(this::registerEssenceDrops);
	}

	private void registerEssenceDrops (FMLStateEvent e) {
		for (ConfigEssence essenceConfig : CONFIG_ESSENCES.essences) {
			if (essenceConfig.bones.type != boneType) {
				continue;
			}

			if (essenceConfig.essence.equals("NONE")) {
				drops.put(null, essenceConfig.bones.dropWeight);
			} else {
				if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(essenceConfig.essence))) {
					drops.put(essenceConfig.essence, essenceConfig.bones.dropWeight);
				} else {
					System.out.println(String
						.format("Colour entry missing for %s:%s", boneType.name(), essenceConfig.essence));
				}
			}
		}

		for (double dropChance : drops.values()) {
			chanceTotal += dropChance;
		}
	}

	private List<ItemStack> getDrops () {
		List<ItemStack> result = new ArrayList<>();
		Random rand = new Random();
		for (Map.Entry<String, Double> dropInfo : drops.entrySet()) {
			if (dropInfo.getKey() == null) continue;

			if (rand.nextFloat() < dropInfo.getValue() / chanceTotal) {
				result.add(Essence.getStack(dropInfo.getKey()));
			}
		}

		return result;
	}

	/////////////////////////////////////////
	// Events
	//

	@Override
	public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!world.isRemote) {
			List<ItemStack> drops = getDrops();
			for (ItemStack drop : drops) {
				EntityItem dropEntity = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
				dropEntity.setNoPickupDelay();
				world.spawnEntity(dropEntity);
			}
		}
		heldItem.setCount(heldItem.getCount() - 1);

		particles(world, player);

		return new ActionResult<>(EnumActionResult.PASS, heldItem);
	}

	@SubscribeEvent
	public static void onHarvest (HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			String blockId = event.getState().getBlock().getRegistryName().toString();
			ConfigFossil fossilConfig = CONFIG_FOSSILS.get(blockId);

			if (fossilConfig != null) {
				List<ItemStack> drops = event.getDrops();
				drops.clear();

				BoneChunk boneChunk = BoneChunk.boneChunkTypes.get(fossilConfig.type);
				int count = new Range(fossilConfig.min * (1 + event
					.getFortuneLevel() / 3), fossilConfig.max * (1 + event.getFortuneLevel() / 3))
						.getInt(event.getWorld().rand);
				drops.add(boneChunk.getItemStack(count));
			}
		}
	}

	/////////////////////////////////////////
	// Util
	//

	private void particles (World world, EntityPlayer player) {
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
				.getIdFromItem(this));
		}

		player.playSound(SoundEvents.BLOCK_GRAVEL_HIT, 0.5F + 0.5F * (float) world.rand
			.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
	}

	/////////////////////////////////////////
	// Jei
	//

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		registry.add(Ingredient.fromItem(this), Soulus.MODID + ":bone_chunk");
	}
}
