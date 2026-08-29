package io.github.chakyl.plentifulponds.events;

import io.github.chakyl.plentifulponds.PlentifulPonds;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = PlentifulPonds.MODID)
public class CommonModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> PlentifulPonds.KUBEJS_INSTALLED = ModList.get().isLoaded("kubejs"));

    }
}