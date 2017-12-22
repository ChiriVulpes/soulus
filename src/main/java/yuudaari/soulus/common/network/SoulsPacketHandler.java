package yuudaari.soulus.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.network.packet.*;

public class SoulsPacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Soulus.MODID);

	public static void register() {
		INSTANCE.registerMessage(BloodCrystalHitEntityHandler.class, BloodCrystalHitEntity.class, 0, Side.CLIENT);
	}
}