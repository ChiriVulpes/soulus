package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ClientField;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/misc", id = Soulus.MODID, path = "bone_chunks")
@Serializable
public class ConfigBoneChunks {

	@Serialized @ClientField public int particleCount = 3;
	@Serialized public boolean sneakToMarrowFullStack = false;
	@Serialized public Range xp = new Range(-8, 4);
}
