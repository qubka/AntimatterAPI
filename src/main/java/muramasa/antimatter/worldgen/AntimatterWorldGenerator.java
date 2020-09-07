package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.worldgen.feature.AntimatterFeature;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    public static void init() {
        Antimatter.LOGGER.info("AntimatterAPI WorldGen Initialization Stage...");
        AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
        if (!AntimatterConfig.WORLD.VANILLA_STONE_GEN) removeStoneFeatures();
        if (!AntimatterConfig.WORLD.VANILLA_ORE_GEN) removeOreFeatures();
        AntimatterAPI.runLaterCommon(() -> {
            AntimatterAPI.all(AntimatterFeature.class).stream().filter(AntimatterFeature::enabled).forEach(f -> {
                f.onDataOverride(new JsonObject());
                f.init();
            });
            WorldGenHelper.init();
        });
        /*
        try {
            //Path config = FMLPaths.CONFIGDIR.get().resolve("GregTech/WorldGenDefault.json");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
         */
    }

    public static void register(Class<?> c, WorldGenBase<?> base) {
        AntimatterFeature<?> feature = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        if (feature != null) base.getDims().forEach(d -> feature.getRegistry().computeIfAbsent(d, k -> new LinkedList<>()).add(base));
    }

    public static <T> List<T> all(Class<T> c, RegistryKey<World> dim) {
        AntimatterFeature<?> feature = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        return feature != null ? feature.getRegistry().computeIfAbsent(dim, k -> new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static void removeStoneFeatures() {
        removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.ANDESITE.getDefaultState(), Blocks.GRANITE.getDefaultState(), Blocks.DIORITE.getDefaultState());
    }

    private static void removeOreFeatures() {
        removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.COAL_ORE.getDefaultState(), Blocks.IRON_ORE.getDefaultState(), Blocks.GOLD_ORE.getDefaultState(), Blocks.REDSTONE_ORE.getDefaultState(), Blocks.LAPIS_ORE.getDefaultState(), Blocks.DIAMOND_ORE.getDefaultState());
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(@Nonnull final GenerationStage.Decoration stage, @Nonnull final Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        AntimatterAPI.runLaterCommon(() -> {
            removeFeature(stage, featureToRemove, states);
        });
    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     * @param biome Biome wish to remove feature from
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiome(@Nonnull final Biome biome, final @Nonnull GenerationStage.Decoration stage, final @Nonnull Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        AntimatterAPI.runLaterCommon(() -> {
            removeFeature(biome, stage, featureToRemove, states);
        });
    }

    /**
     * Check with BlockState in a feature if it is disabled
     */
    public static boolean isDecoratedFeatureDisabled(@Nonnull ConfiguredFeature<?, ?> configuredFeature, @Nonnull Feature<?> featureToRemove, @Nonnull BlockState state) {
        if (configuredFeature.config instanceof DecoratedFeatureConfig) {
            DecoratedFeatureConfig config = (DecoratedFeatureConfig) configuredFeature.config;
            Feature<?> feature = config.feature.get().feature;
            if (feature == featureToRemove) {
                IFeatureConfig featureConfig = config.feature.get().config;
                if (featureConfig instanceof OreFeatureConfig) {
                    BlockState configState = ((OreFeatureConfig) featureConfig).state;
                    return state == configState;
                }
                if (featureConfig instanceof BlockStateFeatureConfig) {
                    BlockState configState = ((BlockStateFeatureConfig) featureConfig).state; // Constructor BlockState var
                    return state == configState;
                }
            }
        }
        return false;
    }

    public static void addFeature(GenerationStage.Decoration stage, Supplier<ConfiguredFeature<?, ?>> featureSupplier) {
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : WorldGenRegistries.field_243657_i.getEntries()) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> lists = entry.getValue().func_242440_e().func_242498_c();
            List<Supplier<ConfiguredFeature<?, ?>>> features = lists.get(stage.ordinal());
            if (features != null) {
                features.add(featureSupplier);
            }
        }
    }

    public static void removeFeature(GenerationStage.Decoration stage, final Feature<?> featureToRemove, BlockState... states) {
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : WorldGenRegistries.field_243657_i.getEntries()) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> lists = entry.getValue().func_242440_e().func_242498_c();
            List<Supplier<ConfiguredFeature<?, ?>>> features = lists.get(stage.ordinal());
            if (features != null) {
                for (BlockState state : states) {
                    features.removeIf(f -> isDecoratedFeatureDisabled(f.get(), featureToRemove, state));
                }
            }
        }
    }

    public static void removeFeature(Biome biome, GenerationStage.Decoration stage, final Feature<?> featureToRemove, BlockState... states) {
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : WorldGenRegistries.field_243657_i.getEntries()) {
            Biome b = entry.getValue();
            if (biome.equals(b)) {
                List<List<Supplier<ConfiguredFeature<?, ?>>>> lists = b.func_242440_e().func_242498_c();
                List<Supplier<ConfiguredFeature<?, ?>>> features = lists.get(stage.ordinal());
                if (features != null) {
                    for (BlockState state : states) {
                        features.removeIf(f -> isDecoratedFeatureDisabled(f.get(), featureToRemove, state));
                    }
                }
            }
        }
    }
}