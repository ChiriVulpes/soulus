package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class ComposerCellAutoFillTrigger extends BasicTrigger<ComposerCellAutoFillTrigger.Instance, Void> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "composer_cell_auto_fill");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public ComposerCellAutoFillTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		return new ComposerCellAutoFillTrigger.Instance();
	}

	public static class Instance extends MatchableCriterionInstance<Void> {

		public Instance () {
			super(ComposerCellAutoFillTrigger.ID);
		}

		public boolean matches (EntityPlayerMP player, Void __) {
			return true;
		}
	}
}
