package yuudaari.soulus.common.advancement;

import java.util.Arrays;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import scala.Tuple3;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;

public class UpgradeTrigger extends BasicTrigger<UpgradeTrigger.Instance, Tuple3<UpgradeableBlock<?>, IUpgrade, Boolean>> {

	private static final ResourceLocation ID = new ResourceLocation(Soulus.MODID, "upgrade");

	@Override
	public ResourceLocation getId () {
		return ID;
	}

	@Override
	public UpgradeTrigger.Instance deserializeInstance (JsonObject json, JsonDeserializationContext context) {
		Block block = null;

		if (!json.has("block")) {
			throw new JsonSyntaxException("Upgrade trigger requires block type");
		}

		ResourceLocation id = new ResourceLocation(JsonUtils.getString(json, "block"));

		if (!Block.REGISTRY.containsKey(id)) {
			throw new JsonSyntaxException("Unknown block type '" + id + "'");
		}

		block = Block.REGISTRY.getObject(id);

		if (!(block instanceof UpgradeableBlock)) {
			throw new JsonSyntaxException("Invalid block type '" + id + "', must be an UpgradeableBlock");
		}

		UpgradeableBlock<?> ublock = (UpgradeableBlock<?>) block;
		String upgrade;
		boolean filled = false;

		if (json.has("upgrade")) {
			upgrade = JsonUtils.getString(json, "upgrade");
			boolean isValidUpgrade = upgrade.equals("*") || Arrays.asList(ublock.getUpgrades())
				.stream()
				.anyMatch(u -> u.getName().equalsIgnoreCase(upgrade));

			if (!isValidUpgrade) {
				throw new JsonSyntaxException("Invalid upgrade type '" + upgrade + "'");
			}

			if (json.has("filled")) {
				filled = JsonUtils.getBoolean(json, "filled");
			}
		} else {
			upgrade = "*";
		}

		return new UpgradeTrigger.Instance(ublock, upgrade, filled);
	}

	public static class Instance extends MatchableCriterionInstance<Tuple3<UpgradeableBlock<?>, IUpgrade, Boolean>> {

		private final UpgradeableBlock<?> block;
		private final String upgrade;
		private final boolean filled;

		public Instance (UpgradeableBlock<?> block, String upgrade, boolean filled) {
			super(UpgradeTrigger.ID);
			this.block = block;
			this.upgrade = upgrade;
			this.filled = filled;
		}

		public boolean matches (EntityPlayerMP player, Tuple3<UpgradeableBlock<?>, IUpgrade, Boolean> upgradeInfo) {
			return upgradeInfo._1().equals(this.block) && //
				(upgrade.equals("*") || upgradeInfo._2().getName().equalsIgnoreCase(upgrade)) && //
				(!filled || upgradeInfo._3());
		}
	}
}
