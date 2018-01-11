package yuudaari.soulus.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.world.World;
import yuudaari.soulus.common.util.Logger;

public class ParticleManager {

	private static Method m;
	static {
		try {
			/*
			Method[] methods = World.class.getDeclaredMethods();
			for (Method method : methods) {
				Logger.info("----");
				Logger.info(method.getName());
				for (Class<?> cls : method.getParameterTypes()) {
					Logger.info(cls.getName());
				}
				if (method.getName() == "spawnParticle" && method.getParameterTypes()[0] == int.class) {
					m = method;
				}
			}
			*/
			Method[] methods = World.class.getDeclaredMethods();
			for (Method method : methods) {
				if ((method.getName().equals("spawnParticle") || method.getName().equals("func_175720_a")) && method
					.getParameterTypes()[0] == int.class) {
					m = method;
				}
			}
			if (m == null) {
				Logger.error("Couldn't find the right method");
			} else {
				m.setAccessible(true);
			}
		} catch (SecurityException e) {
			Logger.error(e);
		}
	}

	public static void spawnParticle (World world, int particleId, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		try {
			if (m != null) {
				m.invoke(world, particleId, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Logger.error(e);
		}
	}
}
