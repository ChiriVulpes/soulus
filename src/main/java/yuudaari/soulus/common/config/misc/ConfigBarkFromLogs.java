package yuudaari.soulus.common.config.misc;

import java.util.ArrayList;
import java.util.List;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.CollectionSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/bark_from_logs", id = Soulus.MODID)
@Serializable
public class ConfigBarkFromLogs {

	@Serialized public float barkChance = 0.01f;
	@Serialized(CollectionSerializer.OfStrings.class) public List<String> logWhitelist = new ArrayList<>();
	{
		logWhitelist.add("minecraft:log");
		logWhitelist.add("minecraft:log2");
	}
}
