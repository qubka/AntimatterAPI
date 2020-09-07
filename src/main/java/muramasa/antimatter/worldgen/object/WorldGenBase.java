package muramasa.antimatter.worldgen.object;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldGenBase<T extends WorldGenBase<?>> {

    private String id;
    private boolean enabled = true;
    private Set<RegistryKey<World>> dims;
    private boolean custom;

    public WorldGenBase() {

    }

    public WorldGenBase(String id, Class<? extends WorldGenBase<?>> c, RegistryKey<World>... dims) {
        this.id = id;
        this.dims = Arrays.stream(dims).collect(Collectors.toCollection(ObjectOpenHashSet::new));
        AntimatterWorldGenerator.register(c, this);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<RegistryKey<World>> getDims() {
        return dims;
    }

    public boolean isCustom() {
        return custom;
    }

    public WorldGenBase<T> asCustom() {
        this.custom = true;
        return this;
    }

    public WorldGenBase<T> onDataOverride(JsonObject json) {
        if (json.has("enabled")) enabled = json.get("enabled").getAsBoolean();
        return this;
    }

    public WorldGenBase<T> build() {
        if (dims == null) throw new IllegalStateException("WorldGenBase - " + id + ": dimensions cannot be null");
        return this;
    }

    public Predicate<Biome> getValidBiomes() {
        return b -> true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldGenBase)) return false;
        WorldGenBase<?> other = (WorldGenBase<?>) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
