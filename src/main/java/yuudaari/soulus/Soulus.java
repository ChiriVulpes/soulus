package yuudaari.soulus;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.client.ModRenderers;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModGenerators;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.advancement.Advancements;
import yuudaari.soulus.common.compat.ExNihiloCreatioRecipes;
import yuudaari.soulus.common.compat.crafttweaker.ZenComposer;
import yuudaari.soulus.common.compat.top.TheOneProbe;
import yuudaari.soulus.common.config.Config;
import yuudaari.soulus.common.misc.BoneChunks;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.SendConfig;
import yuudaari.soulus.common.util.DebugHelper;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.server.command.SoulusCommand;

@Mod(modid = Soulus.MODID, name = Soulus.NAME, version = "@VERSION@", acceptedMinecraftVersions = "[1.12.2]", dependencies = "after:crafttweaker")
@Mod.EventBusSubscriber
public class Soulus {

	static {
		FluidRegistry.enableUniversalBucket();
	}

	public interface PreInitEventHandler {

		void handle (FMLPreInitializationEvent event);
	}

	public interface InitEventHandler {

		void handle (FMLInitializationEvent event);
	}

	public interface PostInitEventHandler {

		void handle (FMLPostInitializationEvent event);
	}

	/* CONSTANTS */

	public static final String NAME = "Soulus";
	public static final String MODID = "soulus";
	public static Config config;

	@Mod.Instance(MODID) public static Soulus INSTANCE;

	/* UTILITY */

	public static ResourceLocation getRegistryName (String name) {
		int index = name.lastIndexOf(':');
		String prefix = index == -1 ? Soulus.MODID : name.substring(0, index);
		name = index == -1 ? name : name.substring(index + 1);
		return new ResourceLocation(prefix, name);
	}

	/* EVENT HANDLERS */

	public static final List<PreInitEventHandler> preInitHandlers = new ArrayList<>();
	public static final List<InitEventHandler> initHandlers = new ArrayList<>();
	public static final List<PostInitEventHandler> postInitHandlers = new ArrayList<>();

	public static void onPreInit (PreInitEventHandler handler) {
		preInitHandlers.add(handler);
	}

	public static void onInit (InitEventHandler handler) {
		initHandlers.add(handler);
	}

	public static void onPostInit (PostInitEventHandler handler) {
		postInitHandlers.add(handler);
	}

	/**
	 * Refreshes the soulus config
	 */
	public static void reloadConfig (boolean syncToClients, boolean serialize) throws Exception {
		config.deserialize();

		if (serialize) {
			try {
				config.serialize();
			} catch (Exception e) {
				Logger.error(e);
			}
		}

		if (syncToClients && FMLCommonHandler.instance().getSide() == Side.SERVER) {
			syncConfigs();
		}
	}

	/**
	 * Synchronises server configs with all clients
	 */
	public static void syncConfigs () {
		SendConfig packet = new SendConfig(Config.INSTANCES.get(Soulus.MODID).SERVER_CONFIGS);
		SoulsPacketHandler.INSTANCE.sendToAll(packet);
	}

	@EventHandler
	public void preinit (final FMLPreInitializationEvent event) {
		if (Loader.isModLoaded("theoneprobe")) {
			TheOneProbe.register();
		}

		final String configPath = event.getModConfigurationDirectory().getAbsolutePath() + "/soulus/";
		config = new Config(event.getAsmData(), configPath, Soulus.MODID);
		try {
			reloadConfig(false, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(INSTANCE, (List<Ticket> tickets, World world) -> {});

		Advancements.registerTriggers();
		DebugHelper.initialize();

		for (final PreInitEventHandler handler : preInitHandlers) {
			handler.handle(event);
		}
	}


	@EventHandler
	public void init (FMLInitializationEvent event) {

		for (InitEventHandler handler : initHandlers) {
			handler.handle(event);
		}
		if (event.getSide() == Side.CLIENT) {
			ModRenderers.init();
		}
		ModGenerators.init();
	}

	@EventHandler
	public void postinit (FMLPostInitializationEvent event) {
		for (PostInitEventHandler handler : postInitHandlers) {
			handler.handle(event);
		}

		SoulsPacketHandler.register();
		if (Loader.isModLoaded("exnihilocreatio")) {
			ExNihiloCreatioRecipes.init();
		}

		if (Loader.isModLoaded("crafttweaker")) {
			ZenComposer.apply();
		}

		BoneChunks.registerEssenceDrops(event);
	}

	@SubscribeEvent
	public static void registerBlocks (RegistryEvent.Register<Block> event) {
		ModBlocks.registerBlocks(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems (RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		ModItems.registerItems(registry);
		ModBlocks.registerItems(registry);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels (ModelRegistryEvent event) {
		ModItems.registerModels();
		ModBlocks.registerModels();
	}

	@SubscribeEvent
	public static void registerRecipes (RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> registry = event.getRegistry();

		ModItems.registerRecipes(registry);
		ModBlocks.registerRecipes(registry);
	}

	@EventHandler
	public void serverLoad (FMLServerStartingEvent event) {
		event.registerServerCommand(new SoulusCommand());
	}

	@SubscribeEvent
	public static void clientConnect (PlayerLoggedInEvent event) {
		SendConfig packet = new SendConfig(Config.INSTANCES.get(Soulus.MODID).SERVER_CONFIGS);
		SoulsPacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public static void disconnect (ClientDisconnectionFromServerEvent event) {
		config.SERVER_CONFIGS.clear();
		try {
			// no need to serialize this time
			reloadConfig(false, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
