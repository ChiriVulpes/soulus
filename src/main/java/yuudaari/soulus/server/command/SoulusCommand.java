package yuudaari.soulus.server.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.GeneratorName;
import yuudaari.soulus.common.util.TextComponentList;

public class SoulusCommand extends CommandBase {

	@Override
	public String getName () {
		return "soulus";
	}

	@Override
	public String getUsage (ICommandSender sender) {
		return "command.soulus:soulus.usage";
	}

	private static final String[] COMMANDS = new String[] {
		"location", "reload"
	};

	@Override
	public List<String> getTabCompletions (MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		List<String> result = new ArrayList<>();

		if (args.length == 1) {
			for (String command : COMMANDS) {
				if (command.startsWith(args[0])) {
					result.add(command);
				}
			}
		}

		return result;
	}

	@Override
	public void execute (MinecraftServer server, ICommandSender ics, String[] args) throws CommandException {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("location")) {
				executeLocation(ics);
				return;

			} else if (args[0].equalsIgnoreCase("reload")) {
				executeReload(ics);
				return;
			}
		}

		throw new SoulusCommandException("soulus", "invalid_syntax", new TextComponentTranslation(getUsage(ics)));
	}

	private void executeReload (ICommandSender ics) throws SoulusCommandException {
		try {
			Soulus.reloadConfig();
			ics.sendMessage(new TextComponentTranslation("command.soulus:reload.success"));

		} catch (final Exception e) {
			throw new SoulusCommandException("reload", "failed", e.getMessage());
		}
	}

	private void executeLocation (ICommandSender ics) {
		List<TextComponentTranslation> result = new ArrayList<>();

		BlockPos pos = ics.getPosition();
		World world = ics.getEntityWorld();
		result.add(new TextComponentTranslation("command.soulus:soulus.location.dimension", //
			world.provider.getDimensionType().getName()));
		result.add(new TextComponentTranslation("command.soulus:soulus.location.separator"));

		Biome biome = world.getBiome(pos);
		ResourceLocation biomeId = biome.getRegistryName();
		result.add(new TextComponentTranslation("command.soulus:soulus.location.biome", //
			biomeId.getResourceDomain(), biomeId.getResourcePath()));
		result.add(new TextComponentTranslation("command.soulus:soulus.location.separator"));

		Set<Type> biomeTypes = BiomeDictionary.getTypes(biome);
		Stack<Object> biomeTypeArgs = new Stack<>();
		for (Type t : biomeTypes) {
			biomeTypeArgs.push(t.getName());
			biomeTypeArgs.push(new TextComponentTranslation("command.soulus:soulus.location.biome_types_separator"));
		}
		biomeTypeArgs.pop();

		result.add(new TextComponentTranslation("command.soulus:soulus.location.biome_types", //
			new TextComponentList(biomeTypeArgs.toArray(new Object[0]))));
		result.add(new TextComponentTranslation("command.soulus:soulus.location.separator"));

		ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
		Stack<Object> structureArgs = new Stack<>();
		for (EventType t : EventType.values()) {
			boolean isInsideStructure = cps.isInsideStructure(world, GeneratorName.get(t.name()), pos);

			// Logger.info("type: " + eventType.name() + ", fixed: " + GeneratorName.get(eventType.name())
			//		+ ", is inside? " + isInsideStructure);

			if (isInsideStructure) {
				structureArgs.push(t.name());
				structureArgs.push(new TextComponentTranslation("command.soulus:soulus.location.structures_separator"));
			}
		}
		result.add(new TextComponentTranslation("command.soulus:soulus.location.structures", //
			structureArgs.size() > 0 ? //
				new TextComponentList(structureArgs.toArray(new Object[0])) : //
				new TextComponentTranslation("command.soulus:soulus.location.structures_none")));

		ics.sendMessage(new TextComponentList(result.toArray(new Object[0])));
	}

	public static class SoulusCommandException extends CommandException {

		private static final long serialVersionUID = 293815792834L;

		public SoulusCommandException (String command, String message, Object... args) {
			super("command.soulus:" + command + "." + message, args);
		}
	}
}
