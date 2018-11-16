package yuudaari.soulus.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class ConstructTrigger extends BasicTrigger<ConstructTrigger.Instance, Block> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "construct");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public ConstructTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		return new ConstructTrigger.Instance(JsonUtils.getString(json, "structure"));
	}

	public static class Instance extends MatchableCriterionInstance<Block> {

		private final String structure;

		public Instance (String structure) {
			super(ConstructTrigger.ID);
			this.structure = structure;
		}

		public boolean matches (EntityPlayerMP player, Block structureBlock) {
			return this.structure.equalsIgnoreCase(structureBlock.getRegistryName().toString());
		}
	}
}
