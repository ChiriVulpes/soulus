package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class BreakSummonerMaliceTrigger extends BasicTrigger<BreakSummonerMaliceTrigger.Instance, Void> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "break_summoner_malice");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public BreakSummonerMaliceTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		return new BreakSummonerMaliceTrigger.Instance();
	}

	public static class Instance extends MatchableCriterionInstance<Void> {

		public Instance () {
			super(BreakSummonerMaliceTrigger.ID);
		}

		public boolean matches (EntityPlayerMP player, Void __) {
			return true;
		}
	}
}
