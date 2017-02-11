package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.Recipes;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class BoneChunk extends Item {
	List<Tuple<Integer, Object>> drops = new ArrayList<>();

	private static Map<String, Integer> spawnMap = new HashMap<>();
	static {
		// passive mobs
		spawnMap.put("Pig", 10);
		spawnMap.put("Chicken", 10);
		spawnMap.put("Sheep", 8);
		spawnMap.put("Cow", 8);
		spawnMap.put("Ocelot", 4);
		spawnMap.put("Rabbit", 4);
		spawnMap.put("Villager", 4);
		spawnMap.put("MushroomCow", 1);

		// neutral mobs
		spawnMap.put("Wolf", 6);
		spawnMap.put("PolarBear", 4);

		// aggressive mobs
		spawnMap.put("Zombie", 2);
		spawnMap.put("Skeleton", 3);
		spawnMap.put("Witch", 1);
	}

	public BoneChunk () {
		super("boneChunk");
		Recipes.remove(new ItemStack(Items.DYE, WILDCARD_VALUE, 15));
		addRecipeShapeless(3, Items.BONE);
		Recipes.addShapeless(new ItemStack(Items.DYE, 1, 15),
			this,
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
		Essence essence = (Essence) ModObjects.getItem("essence");

		spawnMap.forEach((key, spawnChance) -> {
			if (EntityList.ENTITY_EGGS.containsKey(key)) {
				drops.add(new Tuple<>(spawnChance, essence.getStack(key)));
			}
		});
	}

	@Nonnull
	private ItemStack getDrop () {
		int chanceTotal = 0;
		for (Tuple<Integer, Object> drop : drops) {
			chanceTotal += drop.getFirst();
		}
		int choice = new Random().nextInt(chanceTotal);
		for (Tuple<Integer, Object> drop : drops) {
			choice -= drop.getFirst();
			if (choice < 0) {
				if (drop.getSecond() instanceof Item) {
					return ((Item) drop.getSecond()).getItemStack(1);
				} else {
					return (ItemStack) drop.getSecond();
				}
			}
		}
		throw new RuntimeException(String.format("Didn't work remaining: %d total: %d", chanceTotal, choice));
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick (ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			EntityItem dropItem = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, getDrop().copy());
			dropItem.setNoPickupDelay();
			worldIn.spawnEntityInWorld(dropItem);
		}
		itemStackIn.stackSize--;
		return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
	}
}