package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.Recipes;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class BoneChunk extends Item {
	List<Tuple<Integer, Object>> drops = new ArrayList<>();

	public BoneChunk () {
		super("boneChunk");
		Recipes.remove(new ItemStack(Items.DYE, WILDCARD_VALUE, 15));
		addRecipeShapeless(3, Items.BONE);
		Recipes.addShapeless(new ItemStack(Items.DYE, 1, 15),
			this,
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
		Essence essence = (Essence) ModObjects.getItem("essence");

		// passive mobs
		drops.add(new Tuple<>(10, essence.getStack("minecraft:pig")));
		drops.add(new Tuple<>(10, essence.getStack("minecraft:chicken")));
		drops.add(new Tuple<>(8, essence.getStack("minecraft:cow")));
		drops.add(new Tuple<>(8, essence.getStack("minecraft:sheep")));
		drops.add(new Tuple<>(4, essence.getStack("minecraft:ocelot")));
		drops.add(new Tuple<>(4, essence.getStack("minecraft:rabbit")));
		drops.add(new Tuple<>(4, essence.getStack("minecraft:villager")));
		drops.add(new Tuple<>(1, essence.getStack("minecraft:mooshroom")));

		// neutral mobs
		drops.add(new Tuple<>(6, essence.getStack("minecraft:wolf")));
		drops.add(new Tuple<>(4, essence.getStack("minecraft:polar_bear")));

		// aggressive mobs
		drops.add(new Tuple<>(2, essence.getStack("minecraft:zombie")));
		drops.add(new Tuple<>(3, essence.getStack("minecraft:skeleton")));
		drops.add(new Tuple<>(1, essence.getStack("minecraft:witch")));
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