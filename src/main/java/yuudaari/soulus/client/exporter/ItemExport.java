package yuudaari.soulus.client.exporter;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.util.LangHelper;
import yuudaari.soulus.common.util.serializer.ListSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ItemExport {

	@Serialized public final String registryName;
	@Serialized public final String description;
	@Serialized(StacksSerializer.class) public final List<StackExport> stacks;

	public ItemExport (final Item item, final List<StackExport> stacks) {
		this.registryName = item.getRegistryName().toString();
		this.description = LangHelper.localize("jei.description." + registryName);
		this.stacks = stacks;
	}

	@Serializable
	public static class StackExport {

		@Serialized public final String image;
		@Serialized public final String displayName;
		@Serialized public final String nbt;

		public StackExport (final String image, final ItemStack stack) {
			this.image = image;
			this.displayName = stack.getDisplayName();
			this.nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";
		}
	}

	public static class StacksSerializer extends ListSerializer<StackExport> {

		@Override
		public Class<StackExport> getValueClass () {
			return StackExport.class;
		}
	}
}
