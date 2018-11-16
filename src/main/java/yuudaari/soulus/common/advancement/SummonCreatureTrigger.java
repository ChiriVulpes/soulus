package yuudaari.soulus.common.advancement;

import javax.annotation.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import scala.Tuple2;
import yuudaari.soulus.Soulus;

public class SummonCreatureTrigger extends BasicTrigger<SummonCreatureTrigger.Instance, Tuple2<String, Boolean>> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "summon_creature");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public SummonCreatureTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		String essenceType = null;
		Boolean player = null;

		if (json.has("essence_type")) {
			essenceType = JsonUtils.getString(json, "essence_type");
		}

		if (json.has("player")) {
			player = JsonUtils.getBoolean(json, "player");
		}

		return new SummonCreatureTrigger.Instance(essenceType, player);
	}

	public static class Instance extends MatchableCriterionInstance<Tuple2<String, Boolean>> {

		private final String essenceType;
		private final @Nullable Boolean player;

		public Instance (@Nullable String essenceType, @Nullable Boolean player) {
			super(SummonCreatureTrigger.ID);
			this.essenceType = essenceType == null ? "*" : essenceType;
			this.player = player;
		}

		public boolean matches (EntityPlayerMP player, Tuple2<String, Boolean> summonInfo) {
			return (this.essenceType.equals("*") || this.essenceType.equalsIgnoreCase(summonInfo._1())) && //
				(this.player == null || summonInfo._2() == this.player);
		}
	}
}
