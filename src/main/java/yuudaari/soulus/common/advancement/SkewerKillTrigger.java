package yuudaari.soulus.common.advancement;

import javax.annotation.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class SkewerKillTrigger extends BasicTrigger<SkewerKillTrigger.Instance, String> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "skewer_kill");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public SkewerKillTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		String creature = null;

		if (json.has("creature")) {
			creature = JsonUtils.getString(json, "creature");
		}

		return new SkewerKillTrigger.Instance(creature);
	}

	public static class Instance extends MatchableCriterionInstance<String> {

		private final @Nullable String creature;

		public Instance (@Nullable String creature) {
			super(SkewerKillTrigger.ID);
			this.creature = creature;
		}

		public boolean matches (EntityPlayerMP player, String creature) {
			return this.creature == null || this.creature.equals("*") ? true : this.creature.equalsIgnoreCase(creature);
		}
	}
}
