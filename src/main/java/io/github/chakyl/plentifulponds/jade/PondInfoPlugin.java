package io.github.chakyl.plentifulponds.jade;

import io.github.chakyl.plentifulponds.PlentifulPonds;
import io.github.chakyl.plentifulponds.block.FishPondBlock;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

@WailaPlugin
public class PondInfoPlugin implements IWailaPlugin {
    public static final ResourceLocation UID = loc("fish_pond");
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(FishPondInfoComponentProvider.INSTANCE, FishPondBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(FishPondInfoComponentProvider.INSTANCE, FishPondBlock.class);
    }
}