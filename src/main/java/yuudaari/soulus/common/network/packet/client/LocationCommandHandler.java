package yuudaari.soulus.common.network.packet.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.util.TextComponentList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class LocationCommandHandler implements IMessageHandler<LocationCommand, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (LocationCommand message, MessageContext ctx) {
		List<Object> result = new ArrayList<>();

		EntityPlayer player = message.getPlayer();

		BlockPos pos = player.getPosition();
		World world = player.getEntityWorld();
		result.add(new TextComponentString(TextFormatting.GOLD + world.provider.getDimensionType().getName()));

		Biome biome = world.getBiome(pos);
		ResourceLocation biomeId = biome.getRegistryName();
		result.add(new TextComponentString(TextFormatting.RED + biomeId.getResourceDomain()));
		result.add(new TextComponentString(TextFormatting.GOLD + biomeId.getResourcePath()));

		Set<Type> biomeTypes = BiomeDictionary.getTypes(biome);
		Stack<Object> biomeTypeArgs = new Stack<>();
		for (Type t : biomeTypes) {
			biomeTypeArgs.push(new TextComponentString(TextFormatting.RED + t.getName()));
			biomeTypeArgs.push(new TextComponentTranslation("command.soulus:soulus.location.separator"));
		}
		if (biomeTypeArgs.size() > 0) biomeTypeArgs.pop();

		result.add(new TextComponentList(biomeTypeArgs.toArray(new Object[0])));
		//result.add(new TextComponentString("blank"));

		Stack<Object> structureArgs = new Stack<>();
		for (String structure : message.getStructures()) {
			structureArgs.push(new TextComponentString(TextFormatting.RED + structure));
			structureArgs.push(new TextComponentTranslation("command.soulus:soulus.location.separator"));
		}
		if (structureArgs.size() > 0) structureArgs.pop();

		result.add(structureArgs.size() > 0 ? //
			new TextComponentList(structureArgs.toArray(new Object[0])) : //
			new TextComponentTranslation("command.soulus:soulus.location.structures_none"));

		player
			.sendMessage(new TextComponentTranslation("command.soulus:soulus.location", result.toArray(new Object[0])));



		return null;
	}
}
