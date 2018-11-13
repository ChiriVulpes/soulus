package yuudaari.soulus.common.advancement;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;

public class BreakBlockTrigger extends BasicTrigger<BreakBlockTrigger.Instance, IBlockState> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "break_block");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public BreakBlockTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		Block block = null;

		if (json.has("block")) {
			ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "block"));

			if (!Block.REGISTRY.containsKey(resourcelocation)) {
				throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
			}

			block = Block.REGISTRY.getObject(resourcelocation);
		}

		Map<IProperty<?>, Object> map = null;

		if (json.has("state")) {
			if (block == null) {
				throw new JsonSyntaxException("Can't define block state without a specific block type");
			}

			BlockStateContainer blockstatecontainer = block.getBlockState();

			for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "state").entrySet()) {
				IProperty<?> iproperty = blockstatecontainer.getProperty(entry.getKey());

				if (iproperty == null) {
					throw new JsonSyntaxException("Unknown block state property '" + (String) entry
						.getKey() + "' for block '" + Block.REGISTRY.getNameForObject(block) + "'");
				}

				String s = JsonUtils.getString(entry.getValue(), entry.getKey());
				Optional<?> optional = iproperty.parseValue(s);

				if (!optional.isPresent()) {
					throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + (String) entry
						.getKey() + "' on block '" + Block.REGISTRY.getNameForObject(block) + "'");
				}

				if (map == null) {
					map = new HashMap<>();
				}

				map.put(iproperty, optional.get());
			}
		}

		return new BreakBlockTrigger.Instance(block, map);
	}

	public static class Instance extends MatchableCriterionInstance<IBlockState> {

		private final Block block;
		private final Map<IProperty<?>, Object> properties;

		public Instance (@Nullable Block blockIn, @Nullable Map<IProperty<?>, Object> propertiesIn) {
			super(BreakBlockTrigger.ID);
			this.block = blockIn;
			this.properties = propertiesIn;
		}

		public boolean matches (EntityPlayerMP player, IBlockState state) {
			if (this.block != null && state.getBlock() != this.block) {
				return false;
			}

			if (this.properties == null) return true;

			for (Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
				if (state.getValue(entry.getKey()) != entry.getValue()) {
					return false;
				}
			}

			return true;
		}
	}
}
