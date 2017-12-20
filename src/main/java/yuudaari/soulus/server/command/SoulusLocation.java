package yuudaari.soulus.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

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
		BlockPos pos = ics.getPosition();
		World world = ics.getEntityWorld();
		ResourceLocation biome = world.getBiome(pos).getRegistryName();

		ics.sendMessage(new TextComponentString("Dimension: " + TextFormatting.GOLD
				+ world.provider.getDimensionType().getName() + TextFormatting.WHITE + ", Biome: " + TextFormatting.RED
				+ biome.getResourceDomain() + ":" + TextFormatting.GOLD + biome.getResourcePath()));
	}
}