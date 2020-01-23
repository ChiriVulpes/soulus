package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/composer", id = Soulus.MODID, path = "cell")
@Serializable
public class ConfigComposerCell {

	@Serialized public int maxQuantity = 64;
	@Serialized public boolean allowSneakRightClickStackInsertion = true;
	@Serialized public int autoMarrowMaxChunkBuffer = 64;
	@Serialized public Range autoMarrowTicksPerChunkPerOscillatingGear = new Range(100, 1);
	@Serialized public int autoMarrowMaxOscillatingGears = 16;
}
