package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
@Mod.EventBusSubscriber(modid = Ref.ID)
public class DynamicDataPackFinder implements IPackFinder {

    protected final String id, name;

    public DynamicDataPackFinder(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void func_230230_a_(Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory iFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        consumer.accept(ResourcePackInfo.createResourcePack(id, true, () -> dynamicPack, iFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILTIN));
    }

    @SubscribeEvent
    public static void addPackFinder(FMLServerAboutToStartEvent e) {
        AntimatterAPI.runDataProvidersDynamically();
        Antimatter.LOGGER.info("Adding Antimatter's Dynamic Datapack to the server...");
        e.getServer().getResourcePacks().addPackFinder(Ref.SERVER_PACK_FINDER);
        e.getServer().getResourcePacks().getEnabledPacks().forEach(p -> Antimatter.LOGGER.info(p.getName() + " is being loaded into the server..."));
    }

}
