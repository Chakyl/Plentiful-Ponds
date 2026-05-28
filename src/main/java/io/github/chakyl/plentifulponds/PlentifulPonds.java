package io.github.chakyl.plentifulponds;

import dev.shadowsoffire.placebo.tabs.ITabFiller;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.mcexpanded.fancytabsections.FancyTabSections;
import net.mcexpanded.fancytabsections.creativetab.ConglomerateOfItems;
import net.mcexpanded.fancytabsections.creativetab.SectionColored;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static io.github.chakyl.plentifulponds.FancyTabs.addFancyTabSections;
import static io.netty.channel.unix.Unix.registerInternal;
import static net.mcexpanded.fancytabsections.FTSExampleMod.rl;

@Mod(PlentifulPonds.MODID)
public class PlentifulPonds {
    public static final String MODID = "plentifulponds";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public PlentifulPonds(IEventBus bus) {
        bus.register(this);
        ModElements.bootstrap(bus);
        addFancyTabSections();
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            e.enqueueWork(() -> {
                TabFillingRegistry.register(ModElements.Tabs.TAB_KEY, ModElements.Items.FISH_POND, ModElements.Items.SEA_BISCUIT, ModElements.Items.AQUAMARINE, ModElements.Items.ROE, ModElements.Items.AGED_ROE);
            });
        });
        PondRegistry.INSTANCE.registerToBus();
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}