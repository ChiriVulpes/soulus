package yuudaari.soulus.common.compat;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.Logger;

public class GameStages {

	public static boolean tweakConditionMatches (final JsonObject condition) {
		if (!Loader.isModLoaded("gamestages"))
			return false;

		final JsonElement playerJson = condition.get("player");
		if (playerJson == null || !playerJson.isJsonPrimitive() || !playerJson.getAsJsonPrimitive().isString()) {
			Logger.warn("GameStages Tweak conditions must have a 'player' property of either 'all' or 'any'.");
			return false;
		}

		final JsonElement stagesJson = condition.get("stages");
		if (stagesJson == null || !stagesJson.isJsonArray()) {
			Logger.warn("GameStages Tweak conditions must have a 'stages' property containing a list of stages.");
			return false;
		}

		final String playerOption = playerJson.getAsString();
		final JsonArray stages = stagesJson.getAsJsonArray();

		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) return false;

		final PlayerList playerList = server.getPlayerList();
		if (playerList == null) return false;

		for (final EntityPlayer player : server.getPlayerList().getPlayers()) {
			final boolean stagesMatch = stagesMatch(player, stages);

			if (playerOption.equalsIgnoreCase("all") && !stagesMatch)
				return false;

			if (playerOption.equalsIgnoreCase("any") && stagesMatch)
				return true;
		}

		return playerOption.equalsIgnoreCase("all"); // "all" strategy will get here expecting "true", "any" will expect "false"
	}

	private static boolean stagesMatch (final EntityPlayer player, JsonArray stagesJson) {
		final String[] stages = Streams.stream(stagesJson)
			.map(stage -> stage.isJsonPrimitive() && stage.getAsJsonPrimitive().isString() ? stage.getAsString() : null)
			.toArray(String[]::new);

		try {
			return GameStageHelper.hasAllOf(player, stages);

		} catch (final Exception e) {
			Logger.error(e);
			Logger.warn("Are your stages invalid? They must be strings.");
		}

		return false;
	}


	@Optional.Method(modid = "gamestages")
	@SubscribeEvent
	public void added (final GameStageEvent.Added event) {
		changed();
	}

	@Optional.Method(modid = "gamestages")
	@SubscribeEvent
	public void removed (final GameStageEvent.Removed event) {
		changed();
	}

	@Optional.Method(modid = "gamestages")
	@SubscribeEvent
	public void cleared (final GameStageEvent.Cleared event) {
		changed();
	}

	private void changed () {
		Soulus.reloadConfigs(true, false);
	}
}
