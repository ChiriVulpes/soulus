package yuudaari.soulus.common;

import java.util.Arrays;
import java.util.List;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yuudaari.soulus.common.world.ModGenerator;
import yuudaari.soulus.common.world.generators.*;

public class ModGenerators {

	public static final ModGenerator GENERATOR_FOSSILS = new GeneratorFossils();
	public static final List<ModGenerator> generators = Arrays.asList(GENERATOR_FOSSILS);

	public static void init () {
		for (ModGenerator generator : generators) {
			GameRegistry.registerWorldGenerator(generator, 0);
		}
	}
}
