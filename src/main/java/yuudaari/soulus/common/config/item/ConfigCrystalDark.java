package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/crystal_dark", id = Soulus.MODID)
@Serializable
public class ConfigCrystalDark {

	@Serialized public int particleCount = 50;
	@Serialized public float prickChance = 0.001f;
	@Serialized public int prickAmount = 1;
	@Serialized public ModPotionEffect[] prickEffects = new ModPotionEffect[] {
		new ModPotionEffect("nausea", 100),
		new ModPotionEffect("wither", 200)
	};
	@Serialized public ModPotionEffect[] heldEffects = new ModPotionEffect[] {
		new ModPotionEffect("slowness", 100)
	};
}
