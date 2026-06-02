package io.github.chakyl.plentifulponds;

import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(PlentifulPonds.MODID)
public class PlentifulPonds {
    public static final String MODID = "plentifulponds";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public PlentifulPonds(IEventBus bus) {
        bus.register(this);
        ModElements.bootstrap(bus);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            e.enqueueWork(() -> {
                TabFillingRegistry.register(ModElements.Tabs.TAB_KEY, ModElements.Items.FISH_POND, ModElements.Blocks.ROE_RECYCLER, ModElements.Items.SEA_BISCUIT, ModElements.Items.OCEANITE, ModElements.Items.OCEANITE_CLUSTER, ModElements.Items.POND_SCUM, ModElements.Items.ROE, ModElements.Items.AGED_ROE);
            });
        });
        PondRegistry.INSTANCE.registerToBus();
    }

    @SubscribeEvent
    public void caps(RegisterCapabilitiesEvent e) {
        e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModElements.BlockEntities.ROE_RECYCLER, (be, side) -> be.getInventory());
    }
    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}