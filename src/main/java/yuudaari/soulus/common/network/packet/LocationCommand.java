package yuudaari.soulus.common.network.packet;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.common.util.GeneratorName;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;

public class LocationCommand implements IMessage {

	public LocationCommand () {}

	private int entityId;
	private String[] structures;

	public LocationCommand (EntityPlayer player) {
		entityId = player.getEntityId();

		BlockPos pos = player.getPosition();
		World world = player.getEntityWorld();

		ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
		List<String> result = new ArrayList<>();
		for (EventType t : EventType.values()) {
			boolean isInsideStructure = cps.isInsideStructure(world, GeneratorName.get(t.name()), pos);
			if (isInsideStructure) result.add(t.name());
		}

		structures = result.toArray(new String[0]);
	}

	@SideOnly(Side.CLIENT)
	public EntityPlayer getPlayer () {
		return (EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(entityId);
	}

	@SideOnly(Side.CLIENT)
	public String[] getStructures () {
		return structures;
	}

	@Override
	public void toBytes (ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(structures.length);
		for (int i = 0; i < structures.length; i++) {
			buf.writeInt(structures[i].length());
			buf.writeBytes(structures[i].getBytes());
		}
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		entityId = buf.readInt();
		structures = new String[buf.readInt()];
		for (int i = 0; i < structures.length; i++) {
			structures[i] = buf.readBytes(buf.readInt()).toString(Charset.defaultCharset());
		}
	}

}
