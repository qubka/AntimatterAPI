package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DynamicResourcePackFinder implements IPackFinder {

    protected final String id, name, desc;
    protected final boolean hidden;

    public DynamicResourcePackFinder(String id, String name, String desc, boolean hidden) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.hidden = hidden;
    }

    @Override
    public void func_230230_a_(Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory iFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        consumer.accept(new ResourcePackInfo(id, true, () -> dynamicPack, new StringTextComponent(name), new StringTextComponent("Dynamic Resources"), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, false, null, hidden));
    }
}
