package io.github.chakyl.plentifulponds;

import net.neoforged.neoforge.common.ModConfigSpec;


public class PlentifulConfig {

    public final ModConfigSpec.BooleanValue enableAgedRoe;
    // Stages
    public final ModConfigSpec.ConfigValue<String> scum_collector_stage;
    public final ModConfigSpec.ConfigValue<String> mitosis_stage;
    public final ModConfigSpec.ConfigValue<String> hot_hands_stage;
    public final ModConfigSpec.ConfigValue<String> quest_reduction_stage;

    public PlentifulConfig(final ModConfigSpec.Builder builder) {

        enableAgedRoe = builder.comment("Enables the Aged Roe items in the creative menu. Not super useful outside of custom modpacks where a dev makes recipes.").define("general.enable_aged_roe", false);
        // Stages
        scum_collector_stage = builder
                .comment("Requires KubeJs. The stage name with the following impact: Doubles chance of receiving non-roe items from ponds. Can be added to the player using the command e.g, /kubejs stages add playerName scum_collector")
                .define("stages.scum_collector_stage", "scum_collector");
        mitosis_stage = builder
                .comment("Requires KubeJs. The stage name with the following impact: Doubles a fish being retrieved from Fish Ponds if it was born there. Can be added to the player using the command e.g, /kubejs stages add playerName mitosis")
                .define("stages.mitosis_stage", "mitosis");
        hot_hands_stage = builder
                .comment("Requires KubeJs. The stage name with the following impact: Taking out fish born in Fish Ponds smokes them.25%% chance they get charred into coal. Can be added to the player using the command e.g, /kubejs stages add playerName hot_hands")
                .define("stages.hot_hands_stage", "hot_hands");
        quest_reduction_stage = builder
                .comment("Requires KubeJs. The stage name with the following impact: Fish Pond quests will accept half the required items. Can be added to the player using the command e.g, /kubejs stages add playerName pond_house_five")
                .define("stages.quest_reduction_stage", "pond_house_five");
    }
}