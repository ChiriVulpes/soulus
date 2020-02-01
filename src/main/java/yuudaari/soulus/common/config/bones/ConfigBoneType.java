package yuudaari.soulus.common.config.bones;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import scala.Tuple2;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigBoneType {

	@Serialized public String name;
	@Serialized public String itemBone;
	@Serialized public String itemChunk;
	@Serialized public String itemMeal;
	@Serialized public double wolfTameChance;
	@Serialized public boolean canBeMarrowedManually = true;

	public ConfigBoneType () {
	}

	public ConfigBoneType (final String name, final String itemBone, final String itemChunk, final String itemMeal, final double wolfTameChance) {
		this.name = name.toUpperCase();
		this.itemBone = itemBone;
		this.itemChunk = itemChunk;
		this.itemMeal = itemMeal;
		this.wolfTameChance = wolfTameChance;
	}

	public ConfigBoneType setCannotBeMarrowedManually () {
		canBeMarrowedManually = false;
		return this;
	}

	////////////////////////////////////
	// Bones
	//

	public Item getBoneItem () {
		return getItemAndMeta(itemBone)._1();
	}

	public ItemStack getBoneStack () {
		return getBoneStack(1);
	}

	public ItemStack getBoneStack (final int amount) {
		final Tuple2<Item, Integer> bone = getItemAndMeta(itemBone);
		return new ItemStack(bone._1(), amount, bone._2());
	}

	////////////////////////////////////
	// Chunks
	//

	public Item getChunkItem () {
		return getItemAndMeta(itemChunk)._1();
	}

	public ItemStack getChunkStack () {
		return getChunkStack(1);
	}

	public ItemStack getChunkStack (final int amount) {
		final Tuple2<Item, Integer> chunk = getItemAndMeta(itemChunk);
		return new ItemStack(chunk._1(), amount, chunk._2());
	}


	////////////////////////////////////
	// Meals
	//

	public Item getMealItem () {
		return getItemAndMeta(itemMeal)._1();
	}

	public ItemStack getMealStack () {
		return getMealStack(1);
	}

	public ItemStack getMealStack (final int amount) {
		final Tuple2<Item, Integer> meal = getItemAndMeta(itemMeal);
		return new ItemStack(meal._1(), amount, meal._2());
	}

	private Tuple2<Item, Integer> getItemAndMeta (String name) {
		int meta = 0;
		int index = name.indexOf("@");
		if (index >= 0) {
			meta = Integer.parseInt(name.substring(index + 1));
			name = name.substring(0, index);
		}

		return new Tuple2<>(Item.REGISTRY.getObject(new ResourceLocation(name)), meta);
	}

}
