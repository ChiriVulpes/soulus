package yuudaari.soulus;

import yuudaari.soulus.client.ModRenderers;
import yuudaari.soulus.common.config.Config;
import yuudaari.soulus.common.config.SoulConfig;
import yuudaari.soulus.common.compat.ExNihiloCreatio;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModGenerators;
import yuudaari.soulus.common.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = Soulus.MODID, name = Soulus.NAME, acceptedMinecraftVersions = "[1.12.1]")
@Mod.EventBusSubscriber
public class Soulus {

	public interface PreInitEventHandler {
		void handle(FMLPreInitializationEvent event);
	}

	public interface InitEventHandler {
		void handle(FMLInitializationEvent event);
	}

	public interface PostInitEventHandler {
		void handle(FMLPostInitializationEvent event);
	}

	/* CONSTANTS */

	public static final String NAME = "Soulus";
	public static final String MODID = "soulus";
	public static Config config;

	/* UTILITY */

	public static ResourceLocation getRegistryName(String name) {
		int index = name.lastIndexOf(':');
		String prefix = index == -1 ? Soulus.MODID : name.substring(0, index);
		name = index == -1 ? name : name.substring(index + 1);
		return new ResourceLocation(prefix, name);
	}

	public static SoulConfig getSoulInfo(String mobTarget) {
		SoulConfig result = getSoulInfo(mobTarget, true);
		if (result == null)
			throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		return result;
	}

	public static SoulConfig getSoulInfo(String mobTarget, boolean err) {

		SoulConfig result = config.souls.get(mobTarget);

		// try again but without the prefix
		if (result == null && mobTarget.startsWith("minecraft:"))
			result = config.souls.get(mobTarget.substring(10));

		if (result == null && err) {
			throw new RuntimeException("Mob Target '" + mobTarget + "' is invalid");
		}
		return result;
	}

	/* EVENT HANDLERS */

	public static final List<PreInitEventHandler> preInitHandlers = new ArrayList<>();
	public static final List<InitEventHandler> initHandlers = new ArrayList<>();
	public static final List<PostInitEventHandler> postInitHandlers = new ArrayList<>();

	public static void onPreInit(PreInitEventHandler handler) {
		preInitHandlers.add(handler);
	}

	public static void onInit(InitEventHandler handler) {
		initHandlers.add(handler);
	}

	public static void onPostInit(PostInitEventHandler handler) {
		postInitHandlers.add(handler);
	}

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		config = Config.loadConfig(event.getModConfigurationDirectory().getAbsolutePath());
		for (PreInitEventHandler handler : preInitHandlers) {
			handler.handle(event);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		for (InitEventHandler handler : initHandlers) {
			handler.handle(event);
		}
		if (event.getSide() == Side.CLIENT) {
			ModRenderers.init();
		}
		ModGenerators.init();
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		for (PostInitEventHandler handler : postInitHandlers) {
			handler.handle(event);
		}
		ExNihiloCreatio.init();
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.registerBlocks(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		ModItems.registerItems(registry);
		ModBlocks.registerItems(registry);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		ModItems.registerModels();
		ModBlocks.registerModels();
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> registry = event.getRegistry();

		ModItems.registerRecipes(registry);
		ModBlocks.registerRecipes(registry);
	}
}