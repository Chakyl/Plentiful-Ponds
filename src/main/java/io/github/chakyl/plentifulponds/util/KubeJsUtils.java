package io.github.chakyl.plentifulponds.util;

import dev.latvian.mods.kubejs.core.PlayerKJS;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.ModConfigSpec;

public class KubeJsUtils {

    public static boolean kjsPlayerHasStage(ServerPlayer pPlayer, ModConfigSpec.ConfigValue<String> configStage) {
        return ((PlayerKJS) pPlayer).kjs$getStages().has(configStage.get());
    }
}
