package yuudaari.souls;

import yuudaari.souls.common.ModObjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Souls.MODID, name = Souls.NAME, acceptedMinecraftVersions = "[1.12.1]")
public class Souls {
    public static final String NAME = "Souls";
    public static final String MODID = "souls";

    public static final Logger logger = LogManager.getLogger(MODID);

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ModObjects.preinit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModObjects.init();
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        ModObjects.postinit();
    }

    public static ResourceLocation getRegistryName(String name) {
        int index = name.lastIndexOf(':');
        String prefix = index == -1 ? Souls.MODID : name.substring(0, index);
        name = index == -1 ? name : name.substring(index + 1);
        return new ResourceLocation(prefix, name);
    }
}