package yuudaari.souls;

import yuudaari.souls.client.ModRenderers;
import yuudaari.souls.common.ModBlocks;
import yuudaari.souls.common.ModGenerators;
import yuudaari.souls.common.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = Souls.MODID, name = Souls.NAME, acceptedMinecraftVersions = "[1.12.1]")
@Mod.EventBusSubscriber
public class Souls {

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

    public static final String NAME = "Souls";
    public static final String MODID = "souls";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /* UTILITY */

    public static ResourceLocation getRegistryName(String name) {
        int index = name.lastIndexOf(':');
        String prefix = index == -1 ? Souls.MODID : name.substring(0, index);
        name = index == -1 ? name : name.substring(index + 1);
        return new ResourceLocation(prefix, name);
    }

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

    /* EVENT HANDLERS */

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
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
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.registerModels();
        ModItems.registerModels();
    }
}