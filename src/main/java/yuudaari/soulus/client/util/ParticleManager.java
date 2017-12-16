package yuudaari.soulus.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.world.World;
import yuudaari.soulus.common.util.Logger;

public class ParticleManager {
	private static Method m;
	static {
		try {
			m = World.class.getDeclaredMethod("spawnParticle", int.class, boolean.class, double.class, double.class,
					double.class, double.class, double.class, double.class, int[].class);
			m.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.error(e);
		}
	}

	public static void spawnParticle(World world, int particleId, boolean ignoreRange, double xCoord, double yCoord,
			double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		try {
			m.invoke(world, particleId, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Logger.error(e);
		}
	}
}