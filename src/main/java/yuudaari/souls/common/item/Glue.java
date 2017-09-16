package yuudaari.souls.common.item;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import yuudaari.souls.common.util.ModItem;

public class Glue extends ModItem {
	public Glue() {
		super("glue");
		addOreDict("slimeball");
		setFood(1, 0F, true);
		setFoodPotionEffects(new PotionEffect(MobEffects.NAUSEA, 200));
	}
}