package yuudaari.soulus.common.network.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.Tuple2;
import yuudaari.soulus.common.block.skewer.SkewerTileEntity;
import yuudaari.soulus.common.util.Logger;

public class MobSummonHandler implements IMessageHandler<MobSummon, IMessage> {

	public static Stack<Tuple2<Integer, MobSummon>> toProcess = new Stack<>();

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage (MobSummon message, MessageContext ctx) {
		toProcess.push(new Tuple2<>(0, message));

		return null;
	}

	public static void processMobSummons () {
		List<Tuple2<Integer, MobSummon>> newStack = new ArrayList<>();
		while (!toProcess.isEmpty()) {
			Tuple2<Integer, MobSummon> summon = toProcess.pop();

			EntityLivingBase entity = summon._2.getEntity();
			if (entity != null) {
				entity.rotationYaw = summon._2.getRotation();
				Logger.info("set rotation/yaw: " + entity.rotationYaw);
			}

			if (summon._1 < 100) {
				summon = new Tuple2<>(summon._1 + 1, summon._2);
				newStack.add(summon);
			}
		}
		toProcess.addAll(newStack);
		Logger.info("to process: " + toProcess.size());
	}
}
