package yuudaari.soulus.common.item;

import net.minecraft.item.crafting.Ingredient;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.util.ModItem;

public class GearBone extends ModItem {

	public GearBone (String name) {
		super(name);
		addOreDict("gearBone");
	}

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		registry.add(Ingredient.fromItem(this), Soulus.MODID + ":gear_bone");
	}
}
