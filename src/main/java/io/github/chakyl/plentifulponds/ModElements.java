package io.github.chakyl.plentifulponds;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.block.FishPondBlock;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.item.AgedRoeItem;
import io.github.chakyl.plentifulponds.item.RoeItem;
import io.github.chakyl.plentifulponds.screen.FishPondMenu;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

public class ModElements {
    private static final DeferredHelper R = DeferredHelper.create(PlentifulPonds.MODID);


    private static final BlockBehaviour.StatePredicate ALWAYS_FALSE = (state, world, pos) -> false;


    public static class Blocks {
        public static final Holder<Block> FISH_POND = R.block("fish_pond", FishPondBlock::new, p -> p.strength(4, 3000).noOcclusion().isRedstoneConductor((state, lvl, pos) -> false));

        private static void bootstrap() {
        }
    }

    public static class BlockEntities {
        public static final BlockEntityType<FishPondBlockEntity> FISH_POND = R.tickingBlockEntity("fish_pond", FishPondBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, ModElements.Blocks.FISH_POND);

        private static void bootstrap() {
        }
    }

    public static class Items {
        public static final Holder<Item> FISH_POND = R.blockItem("fish_pond", Blocks.FISH_POND);

        public static final Holder<Item> AQUAMARINE = R.item("aquamarine", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
        public static final Holder<Item> SEA_BISCUIT = R.item("sea_biscuit", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final Holder<Item> ROE = R.item("roe", RoeItem::new, p -> p.stacksTo(64));
        public static final Holder<Item> AGED_ROE = R.item("aged_roe", AgedRoeItem::new, p -> p.stacksTo(64).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).build()));

        private static void bootstrap() {
        }
    }


    public static class Menus {
        public static final MenuType<FishPondMenu> FISH_POND = R.menuWithPos("fish_pond", FishPondMenu::new);

        private static void bootstrap() {
        }
    }

    public static class Components {
        /**
         * Stored fish pond type, used by {@link RoeItem} and {@link AgedRoeItem}. The underlying holder may be unbound.
         */
        public static final DataComponentType<DynamicHolder<Pond>> FISH = R.component("pond", b -> b
                .persistent(PondRegistry.INSTANCE.holderCodec())
                .networkSynchronized(PondRegistry.INSTANCE.holderStreamCodec()));


        private static void bootstrap() {
        }
    }

    public static class Tabs {
        public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, loc("tab"));

        public static final Holder<CreativeModeTab> TAB = R.creativeTab("tab", b -> b.title(Component.translatable("itemGroup." + PlentifulPonds.MODID)).icon(() -> Items.FISH_POND.value().getDefaultInstance()));

        private static void bootstrap() {
        }
    }

    static void bootstrap(IEventBus bus) {
        bus.register(R);
        Blocks.bootstrap();
        BlockEntities.bootstrap();
        Items.bootstrap();
        Menus.bootstrap();
        Components.bootstrap();
        Tabs.bootstrap();
    }
}