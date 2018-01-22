package yuudaari.soulus.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.network.packet.*;

public class SoulsPacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Soulus.MODID);

	public static void register () {
		INSTANCE.registerMessage(CrystalBloodHitEntityHandler.class, CrystalBloodHitEntity.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(MobPoofHandler.class, MobPoof.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(LocationCommandHandler.class, LocationCommand.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(CrystalDarkPrickHandler.class, CrystalDarkPrick.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(TetherEntityHandler.class, TetherEntity.class, 4, Side.CLIENT);
		INSTANCE.registerMessage(MobSummonHandler.class, MobSummon.class, 5, Side.CLIENT);
	}
}
