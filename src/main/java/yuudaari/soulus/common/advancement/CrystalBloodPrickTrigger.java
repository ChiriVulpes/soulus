package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class CrystalBloodPrickTrigger extends BasicTrigger<CrystalBloodPrickTrigger.Instance, Void> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "crystal_blood_prick");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public CrystalBloodPrickTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		return new CrystalBloodPrickTrigger.Instance();
	}

	public static class Instance extends MatchableCriterionInstance<Void> {

		public Instance () {
			super(CrystalBloodPrickTrigger.ID);
		}

		public boolean matches (EntityPlayerMP player, Void __) {
			return true;
		}
	}
}
