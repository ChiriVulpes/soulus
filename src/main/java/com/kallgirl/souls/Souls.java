package com.kallgirl.souls;

import com.kallgirl.souls.common.ModObjects;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION)
public class Souls {

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
}