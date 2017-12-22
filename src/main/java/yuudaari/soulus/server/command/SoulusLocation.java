package yuudaari.soulus.server.command;

import java.util.ArrayList;
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
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

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
		result.add("Biome Types: " + TextFormatting.RED + biomeTypes.stream().map(BiomeDictionary.Type::getName)
				.collect(Collectors.joining(TextFormatting.WHITE + ", " + TextFormatting.RED)));

		ics.sendMessage(new TextComponentString(String.join(TextFormatting.WHITE + ", ", result)));
	}
}