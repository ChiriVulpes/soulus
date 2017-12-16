package yuudaari.souls.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuudaari.souls.common.network.packet.*;

public class SoulsPacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("souls");

	static {
		INSTANCE.registerMessage(BloodCrystalHitEntity.Handler.class, BloodCrystalHitEntity.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(SummonerChangeMob.Handler.class, SummonerChangeMob.class, 0, Side.CLIENT);
	}
}