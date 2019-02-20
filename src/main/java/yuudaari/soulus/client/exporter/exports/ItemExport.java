package yuudaari.soulus.client.exporter.exports;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.util.LangHelper;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.common.util.serializer.CollectionSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ItemExport {

	@Serialized public final String registryName;
	@Serialized public final boolean isBlock;
	@Serialized public final String description;
	@Serialized(StackListSerializer.class) public final List<StackExport> stacks;

	public ItemExport (final Item item, final List<StackExport> stacks) {
		this.registryName = item.getRegistryName().toString();
		this.isBlock = item instanceof ItemBlock;
		this.stacks = stacks;

		String registryName = null;
		if (item instanceof ModItem) {
			registryName = ((ModItem) item).getDescriptionRegistryName();
		}

		this.description = LangHelper.localize("jei.description." + (registryName == null ? this.registryName : registryName));
	}

	@Serializable
	public static class StackExport {

		@Serialized public final String image;
		@Serialized public final String displayName;
		@Serialized public final String nbt;
		@Serialized public final int data;

		public StackExport (final String image, final ItemStack stack) {
			this.image = image;
			this.displayName = stack.getDisplayName();
			this.data = stack.getMetadata();
			this.nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";
		}
	}

	public static class StackListSerializer extends CollectionSerializer<StackExport> {

		@Override
		public Class<StackExport> getValueClass () {
			return StackExport.class;
		}
	}
}
