package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ClientField;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/crystal_blood", id = Soulus.MODID)
@Serializable
public class ConfigCrystalBlood extends ConfigItem {

	{
		stackSize = 16;
	}

	// CLIENT
	@Serialized @ClientField public int particleCount = 50;

	// SERVER
	@Serialized public int requiredBlood = 1000;
	@Serialized public int prickAmount = 9;
	@Serialized public int prickWorth = 100;
	@Serialized public int creaturePrickRequiredHealth = 9999999;
	@Serialized public int creaturePrickAmount = 1;
	@Serialized public int creaturePrickWorth = 3;
	@Serialized public ModPotionEffect[] prickEffects = new ModPotionEffect[] {
		new ModPotionEffect("hunger", 100),
		new ModPotionEffect("nausea", 200),
		new ModPotionEffect("mining_fatigue", 600).setIsTiered(),
	};
}
