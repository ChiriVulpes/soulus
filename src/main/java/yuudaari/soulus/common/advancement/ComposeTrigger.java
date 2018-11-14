package yuudaari.soulus.common.advancement;

import javax.annotation.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class ComposeTrigger extends BasicTrigger<ComposeTrigger.Instance, String> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "compose");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public ComposeTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		String recipe = null;

		if (json.has("recipe")) {
			recipe = JsonUtils.getString(json, "recipe");
		}

		return new ComposeTrigger.Instance(recipe);
	}

	public static class Instance extends MatchableCriterionInstance<String> {

		private final @Nullable String recipe;

		public Instance (@Nullable String recipe) {
			super(ComposeTrigger.ID);
			this.recipe = recipe;
		}

		public boolean matches (EntityPlayerMP player, String recipe) {
			return this.recipe == null || this.recipe.equals("*") ? true : this.recipe.equalsIgnoreCase(recipe);
		}
	}
}
