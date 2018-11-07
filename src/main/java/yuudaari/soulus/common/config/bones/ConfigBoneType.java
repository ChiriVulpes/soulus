package yuudaari.soulus.common.config.bones;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigBoneType {

	@Serialized public String name;
	@Serialized public String item_bone;
	@Serialized public String item_chunk;

	public ConfigBoneType () {}

	public ConfigBoneType (String name, String item_bone, String item_chunk) {
		this.name = name.toUpperCase();
		this.item_bone = item_bone;
		this.item_chunk = item_chunk;
	}

	public ItemStack getBoneStack () {
		return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(item_bone)));
	}

	public ItemStack getChunkStack () {
		return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(item_chunk)));
	}

	public ItemStack getBoneStack (int amount) {
		return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(item_bone)), amount);
	}

	public ItemStack getChunkStack (int amount) {
		return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(item_chunk)), amount);
	}

}
