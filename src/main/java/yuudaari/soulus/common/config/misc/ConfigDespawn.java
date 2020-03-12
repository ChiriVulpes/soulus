package yuudaari.soulus.common.config.misc;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/despawn", id = Soulus.MODID)
@Serializable
public class ConfigDespawn {

	@Serialized public boolean despawnMobsSummoned = false;
	@Serialized public boolean despawnMobsFromEggs = false;
}
