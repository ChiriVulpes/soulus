package yuudaari.soulus.common.config.bones;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigBoneType {

	@Serialized public String name;
	@Serialized public String itemBone;
	@Serialized public String itemChunk;
	@Serialized public double wolfTameChance;

	public ConfigBoneType () {
	}

	public ConfigBoneType (final String name, final String itemBone, final String itemChunk, final double wolfTameChance) {
		this.name = name.toUpperCase();
		this.itemBone = itemBone;
		this.itemChunk = itemChunk;
		this.wolfTameChance = wolfTameChance;
	}

	////////////////////////////////////
	// Items
	//

	public Item getBoneItem () {
		return Item.REGISTRY.getObject(new ResourceLocation(itemBone));
	}

	public Item getChunkItem () {
		return Item.REGISTRY.getObject(new ResourceLocation(itemChunk));
	}

	////////////////////////////////////
	// Stacks
	//

	public ItemStack getBoneStack () {
		return new ItemStack(getBoneItem());
	}

	public ItemStack getChunkStack () {
		return new ItemStack(getChunkItem());
	}

	public ItemStack getBoneStack (final int amount) {
		return new ItemStack(getBoneItem(), amount);
	}

	public ItemStack getChunkStack (final int amount) {
		return new ItemStack(getChunkItem(), amount);
	}

}
