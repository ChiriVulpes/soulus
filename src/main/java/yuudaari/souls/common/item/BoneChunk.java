package yuudaari.souls.common.item;

import yuudaari.souls.common.BoneType;
import yuudaari.souls.common.Config;
import yuudaari.souls.common.ModObjects;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BoneChunk extends SoulsItem {
	private Map<Object, Integer> drops = new HashMap<>();
	private int chanceTotal = 0;
	private BoneType boneType;

	public BoneChunk(String name, BoneType boneType) {
		super(name);
		this.boneType = boneType;
	}

	@Override
	public void init() {
		// initialize drops
		Essence essence = (Essence) ModObjects.getItem("essence");
		Config.spawnMap.get(boneType).forEach((entityName, spawnInfo) -> {
			if (entityName.equals("none")) {
				drops.put(null, spawnInfo.dropChance);
			} else {
				if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation("minecraft", entityName))
						|| spawnInfo.colourInfo != null) {
					drops.put(essence.getStack(entityName), spawnInfo.dropChance);
				} else {
					System.out.println(String.format("Colour entry missing for %s:%s", boneType.name(), entityName));
				}
			}
		});

		for (Map.Entry<Object, Integer> drop : drops.entrySet()) {
			chanceTotal += drop.getValue();
		}
	}

	@Nullable
	private ItemStack getDrop() {
		int choice = new Random().nextInt(chanceTotal);
		for (Map.Entry<Object, Integer> dropInfo : drops.entrySet()) {
			choice -= dropInfo.getValue();
			if (choice < 0) {
				Object drop = dropInfo.getKey();
				if (drop != null) {
					return drop instanceof SoulsItem ? ((SoulsItem) drop).getItemStack() : ((ItemStack) drop).copy();
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
		return new ActionResult<>(EnumActionResult.PASS, heldItem);
	}
}