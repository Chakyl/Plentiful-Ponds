package io.github.chakyl.plentifulponds.util;

import io.github.chakyl.plentifulponds.PlentifulPonds;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GeneralUtils {

    public static boolean playerHasStage(ServerPlayer pPlayer, ModConfigSpec.ConfigValue<String> configStage) {
        if (!PlentifulPonds.KUBEJS_INSTALLED) return false;
        return KubeJsUtils.kjsPlayerHasStage(pPlayer, configStage);
    }

}
