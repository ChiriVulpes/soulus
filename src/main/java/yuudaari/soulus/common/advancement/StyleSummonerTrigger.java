package yuudaari.soulus.common.advancement;

import javax.annotation.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class StyleSummonerTrigger extends BasicTrigger<StyleSummonerTrigger.Instance, String> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "style_summoner");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public StyleSummonerTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		String style = null;

		if (json.has("style")) {
			style = JsonUtils.getString(json, "style");
		}

		return new StyleSummonerTrigger.Instance(style);
	}

	public static class Instance extends MatchableCriterionInstance<String> {

		private final @Nullable String style;

		public Instance (@Nullable String style) {
			super(StyleSummonerTrigger.ID);
			this.style = style;
		}

		public boolean matches (EntityPlayerMP player, String style) {
			return this.style == null || this.style.equals("*") ? true : this.style.equalsIgnoreCase(style);
		}
	}
}
