package io.github.chakyl.plentifulponds.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import io.github.chakyl.plentifulponds.PlentifulPonds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

public class PondRegistry extends DynamicRegistry<Pond> {

    public static final PondRegistry INSTANCE = new PondRegistry();
    private Map<String, Pond> pondTypes = new HashMap<>();

    public PondRegistry() {
        super(PlentifulPonds.LOGGER, "ponds", true, true);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(loc("ponds"), Pond.CODEC);
    }

    @Override
    protected void beginReload(ReloadType type) {
        super.beginReload(type);
        this.pondTypes = new HashMap<>();
    }

    @Override
    protected void onReload(ReloadType type) {
        super.onReload(type);
        this.pondTypes = ImmutableMap.copyOf(this.pondTypes);
    }

    @Override
    protected void validateItem(ResourceLocation key, Pond pond) {
        pond.validate(key);
        if (this.pondTypes.containsKey(pond.fish().getDescriptionId())) {
            String msg = "Attempted to register two ponds (%s and %s) for pond IDs %s!";
            throw new UnsupportedOperationException(String.format(msg, key, this.getKey(this.pondTypes.get(pond.fish().getDescriptionId())), pond.fish()));
        }
        this.pondTypes.put(pond.fish().getDescriptionId(), pond);
    }

    public boolean isPondFish(Item item) {
        if (item == null) return false;
        return this.pondTypes.get(item.getDescriptionId()) != null;
    }

    public Pond getForItem(Item item) {
        if (item == null) return null;
        return this.pondTypes.get(item.getDescriptionId());
    }

    @Override
    public Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return super.prepare(pResourceManager, pProfiler);
    }

}