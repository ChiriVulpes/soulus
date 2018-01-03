package yuudaari.soulus.server.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import yuudaari.soulus.common.util.GeneratorName;

public class SoulusLocation extends CommandBase {

	@Override
	public String getName() {
		return "souluslocation";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Usage: /" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException {
		List<String> result = new ArrayList<>();

		BlockPos pos = ics.getPosition();
		World world = ics.getEntityWorld();
		result.add("Dimension: " + TextFormatting.GOLD + world.provider.getDimensionType().getName());

		Biome biome = world.getBiome(pos);
		ResourceLocation biomeId = biome.getRegistryName();
		result.add("Biome: " + TextFormatting.RED + biomeId.getResourceDomain() + ":" + TextFormatting.GOLD
				+ biomeId.getResourcePath());

		Set<Type> biomeTypes = BiomeDictionary.getTypes(biome);
		result.add("Biome Types: " + TextFormatting.RED + biomeTypes.stream().map(b -> b.getName())
				.collect(Collectors.joining(TextFormatting.WHITE + ", " + TextFormatting.RED)));

		ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
		result.add("Structures: " + TextFormatting.RED + Arrays.asList(EventType.values()).stream().map(eventType -> {
			boolean isInsideStructure = cps.isInsideStructure(world, GeneratorName.get(eventType.name()), pos);

			// Logger.info("type: " + eventType.name() + ", fixed: " + GeneratorName.get(eventType.name())
			//		+ ", is inside? " + isInsideStructure);

			return isInsideStructure ? eventType.name() : null;
		}).filter(eventTypeName -> eventTypeName != null)
				.collect(Collectors.joining(TextFormatting.WHITE + ", " + TextFormatting.RED)));

		if (result.remove("Structures: " + TextFormatting.RED))
			result.add("Structures: None");

		ics.sendMessage(new TextComponentString(String.join(TextFormatting.WHITE + ", ", result)));
	}
}