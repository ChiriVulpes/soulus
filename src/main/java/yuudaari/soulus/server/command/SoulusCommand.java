package yuudaari.soulus.server.command;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.LocationCommand;

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
		Entity e = ics.getCommandSenderEntity();
		if (e instanceof EntityPlayerMP) {
			SoulsPacketHandler.INSTANCE.sendTo(new LocationCommand((EntityPlayerMP) e), (EntityPlayerMP) e);
		}
	}

	public static class SoulusCommandException extends CommandException {

		private static final long serialVersionUID = 293815792834L;

		public SoulusCommandException (String command, String message, Object... args) {
			super("command.soulus:" + command + "." + message, args);
		}
	}
}
