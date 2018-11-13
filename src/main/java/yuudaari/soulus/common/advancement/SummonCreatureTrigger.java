package yuudaari.soulus.common.advancement;

import javax.annotation.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class SummonCreatureTrigger extends BasicTrigger<SummonCreatureTrigger.Instance, String> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "summon_creature");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public SummonCreatureTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		String essenceType = null;

		if (json.has("essence_type")) {
			essenceType = JsonUtils.getString(json, "essence_type");
		}

		return new SummonCreatureTrigger.Instance(essenceType);
	}

	public static class Instance extends MatchableCriterionInstance<String> {

		private final @Nullable String essenceType;

		public Instance (@Nullable String essenceType) {
			super(SummonCreatureTrigger.ID);
			this.essenceType = essenceType;
		}

		public boolean matches (EntityPlayerMP player, String essenceType) {
			return this.essenceType == null ? false : //
				this.essenceType.equals("*") ? true : this.essenceType.equalsIgnoreCase(essenceType);
		}
	}
}
