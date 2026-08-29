package io.github.chakyl.plentifulponds;

import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.item.AgedRoeItem;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.mcexpanded.fancytabsections.FancyTabSections;
import net.mcexpanded.fancytabsections.Section.SectionColored;
import net.mcexpanded.fancytabsections.creativetab.ConglomerateOfItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


@Mod(PlentifulPonds.MODID)
public class PlentifulPonds {
    public static final String MODID = "plentifulponds";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ConglomerateOfItems roe = ConglomerateOfItems.create();
    private static final ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();
    public static final PlentifulConfig CONFIG = new PlentifulConfig(CONFIG_BUILDER);
    public static boolean KUBEJS_INSTALLED = false;

    public PlentifulPonds(ModContainer container) {
        IEventBus bus = container.getEventBus();
        container.registerConfig(ModConfig.Type.COMMON, CONFIG_BUILDER.build());
        bus.register(this);
        ModElements.bootstrap(bus);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        PondRegistry.INSTANCE.registerToBus();
        e.enqueueWork(() -> {
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("equipment"))
                            .setTitle(Component.translatable("Equipment"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add(ModElements.Items.FISH_POND.value())
                            .add(ModElements.Items.ROE_RECYCLER.value())
                            .add(ModElements.Items.SEA_BISCUIT.value()));

            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("materials"))
                            .setTitle(Component.translatable("Materials"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add(ModElements.Items.OCEANITE.value())
                            .add(ModElements.Items.OCEANITE_CLUSTER.value())
                            .add(ModElements.Items.POND_SCUM.value()));
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("roe"))
                            .setTitle(Component.translatable("Roe"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add((registry) -> PondRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(PondRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(ModElements.Items.ROE);
                                        RoeItem.setStoredFish(s, holder);
                                        return s;
                                    })
                                    .toList())
                            .add((registry) -> CONFIG.enableAgedRoe.get() ? PondRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(PondRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(ModElements.Items.AGED_ROE);
                                        AgedRoeItem.setStoredFish(s, holder);
                                        return s;
                                    })
                                    .toList() : List.of()));
        });
    }

    @SubscribeEvent
    public void caps(RegisterCapabilitiesEvent e) {
        e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModElements.BlockEntities.ROE_RECYCLER, (be, side) -> be.getInventory());
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}