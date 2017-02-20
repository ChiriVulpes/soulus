package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.BoneType;
import com.kallgirl.souls.common.Config;
import com.kallgirl.souls.common.ModObjects;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BoneChunk extends Item {
	private Map<Object, Integer> drops = new HashMap<>();
	private int chanceTotal = 0;

	public BoneChunk (String name, BoneType boneType) {
		super(name);

		// initialize drops
		Essence essence = (Essence) ModObjects.getItem("essence");
		Config.spawnMap.get(boneType).forEach((entityName, spawnInfo) -> {
			if (entityName.equals("none")) {
				drops.put(null, spawnInfo.dropChance);
			} else {
				// fix entity names so they match the actual entity ids
				String fixed = Config.EntityIdMap.get(entityName);
				if (fixed != null) entityName = fixed;

				if (EntityList.ENTITY_EGGS.containsKey(entityName) || spawnInfo.colourInfo != null) {
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
	private ItemStack getDrop () {
		int choice = new Random().nextInt(chanceTotal);
		for (Map.Entry<Object, Integer> dropInfo : drops.entrySet()) {
			choice -= dropInfo.getValue();
			if (choice < 0) {
				Object drop = dropInfo.getKey();
				if (drop != null) {
					return drop instanceof Item ? ((Item) drop).getItemStack() : ((ItemStack) drop).copy();
				}
				return null;
			}
		}
		throw new RuntimeException("Bonechunk drop failed!");
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick (ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			ItemStack drop = getDrop();
			if (drop != null) {
				EntityItem dropEntity = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, drop);
				dropEntity.setNoPickupDelay();
				worldIn.spawnEntityInWorld(dropEntity);
			}
		}
		itemStackIn.stackSize--;
		return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
	}
}