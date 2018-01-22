package yuudaari.soulus.common.config.creature;

import java.util.ArrayList;
import java.util.List;
import yuudaari.soulus.common.util.serializer.ListSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreatureDrops {

	@Serialized public boolean hasXp = true;
	@Serialized(ListSerializer.OfStrings.class) public List<String> whitelistedDrops = new ArrayList<>();
	@Serialized(ListSerializer.OfStrings.class) public List<String> blacklistedDrops = new ArrayList<>();

	public ConfigCreatureDrops () {}

	public ConfigCreatureDrops (final boolean whitelistAll) {
		if (whitelistAll) {
			whitelistedDrops.add("*");
		}
	}
}
