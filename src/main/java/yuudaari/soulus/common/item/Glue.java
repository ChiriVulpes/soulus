package yuudaari.soulus.common.item;

import yuudaari.soulus.common.config_old.PotionEffectSerializer;
import yuudaari.soulus.common.config_old.Serializer;
import yuudaari.soulus.common.util.ModItem;
import yuudaari.soulus.common.util.ModPotionEffect;

public class Glue extends ModItem {

	public static Serializer<Glue> serializer;
	static {
		serializer = new Serializer<>(Glue.class, "foodAmount", "foodSaturation", "foodDuration", "foodQuantity", "foodAlwaysEdible");

		serializer.fieldHandlers.put("foodEffects", PotionEffectSerializer.INSTANCE);
	}

	public Glue () {
		super("glue");
		addOreDict("slimeball");
		setFood(1, 0F);
		foodAlwaysEdible = true;
		foodEffects = new ModPotionEffect[] {
			new ModPotionEffect("nausea", 200)
		};
		setHasDescription();
	}
}
