package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class ComposerCellAutoMarrowTrigger extends BasicTrigger<ComposerCellAutoMarrowTrigger.Instance, Void> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "composer_cell_auto_marrow");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public ComposerCellAutoMarrowTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		return new ComposerCellAutoMarrowTrigger.Instance();
	}

	public static class Instance extends MatchableCriterionInstance<Void> {

		public Instance () {
			super(ComposerCellAutoMarrowTrigger.ID);
		}

		public boolean matches (EntityPlayerMP player, Void __) {
			return true;
		}
	}
}
