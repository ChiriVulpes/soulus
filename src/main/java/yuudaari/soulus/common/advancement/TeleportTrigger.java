package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class TeleportTrigger extends BasicTrigger<TeleportTrigger.Instance, Integer> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "teleport");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public TeleportTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		int teleports = -1;

		if (json.has("teleports")) {
			teleports = JsonUtils.getInt(json, "teleports");
		}

		return new TeleportTrigger.Instance(teleports);
	}

	public static class Instance extends MatchableCriterionInstance<Integer> {

		private final int teleports;

		public Instance (int teleports) {
			super(TeleportTrigger.ID);
			this.teleports = teleports;
		}

		public boolean matches (EntityPlayerMP player, Integer teleports) {
			return teleports >= this.teleports;
		}
	}
}
