package io.github.chakyl.plentifulponds.events;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.PlentifulPonds;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.item.AgedRoeItem;
import io.github.chakyl.plentifulponds.item.RoeItem;
import io.github.chakyl.plentifulponds.ModElements;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;


@EventBusSubscriber(value = Dist.CLIENT, modid = PlentifulPonds.MODID)
public class ClientModEvents {

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ModelResourceLocation.standalone(loc("item/roe")));
        e.register(ModelResourceLocation.standalone(loc("item/aged_roe")));
    }

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item e) {
        e.register((stack, tint) -> {
            DynamicHolder<Pond> model = RoeItem.getStoredFish(stack);
            int color = 0xFFFFFF;
            if (model.isBound()) {
                color = model.get().color().getValue();
            }
            return 0xFF000000 | color;
        }, ModElements.Items.ROE.value());
        e.register((stack, tint) -> {
            DynamicHolder<Pond> model = AgedRoeItem.getStoredFish(stack);
            int color = 0xFFFFFF;
            if (model.isBound()) {
                color = model.get().color().getValue();
            }
            return 0xFF000000 | color;
        }, ModElements.Items.AGED_ROE.value());
    }

}