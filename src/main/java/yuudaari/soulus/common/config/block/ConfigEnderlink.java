package yuudaari.soulus.common.config.block;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ClientField;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(id = Soulus.MODID, file = "block/enderlink")
@Serializable
public class ConfigEnderlink {

	// CLIENT
	@Serialized @ClientField public int particleCountTeleport = 15;
	@Serialized @ClientField public int particleCountTeleportItem = 4;

	// SERVER
	@Serialized public int nonUpgradedRange = 16;
	@Serialized public int upgradeRangeEffectiveness = 1;
	@Serialized public double teleportChance = 0.05;
}
