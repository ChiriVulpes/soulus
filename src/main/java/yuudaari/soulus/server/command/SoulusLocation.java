package yuudaari.soulus.server.command;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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

		ics.sendMessage(new TextComponentString("Dimension: " + ChatFormatting.GOLD
				+ world.provider.getDimensionType().getName() + ChatFormatting.WHITE + ", Biome: " + ChatFormatting.RED
				+ biome.getResourceDomain() + ":" + ChatFormatting.GOLD + biome.getResourcePath()));
	}
}