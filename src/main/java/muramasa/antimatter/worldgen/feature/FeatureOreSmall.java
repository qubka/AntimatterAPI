package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import net.minecraft.block.BlockState;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class FeatureOreSmall extends AntimatterFeature<NoFeatureConfig> {

    public FeatureOreSmall() {
        super(NoFeatureConfig.field_236558_a_, WorldGenOreSmall.class);
    }

    @Override
    public String getId() {
        return "feature_ore_small";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.SMALL_ORES && getRegistry().size() > 0;
    }

    @Override
    public void init() {
        AntimatterWorldGenerator.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, () -> new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
    }

    @Override
    public boolean func_241855_a(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        World world = reader.getWorld();
        List<WorldGenOreSmall> ores = AntimatterWorldGenerator.all(WorldGenOreSmall.class, world.getDimensionKey());
        BlockPos.Mutable mut = new BlockPos.Mutable();
        int amount;
        BlockState existing;
        for (WorldGenOreSmall ore : ores) {
            amount = Math.max(1, ore.getAmount() / 2 + rand.nextInt(1 + ore.getAmount()) / 2);
            for (int i = 0; i < amount; i++) {
                mut.setPos(pos.getX() + rand.nextInt(16), ore.getMinY() + rand.nextInt(Math.max(1, ore.getMaxY() - ore.getMinY())), pos.getZ() + rand.nextInt(16));
                existing = world.getBlockState(mut);
                WorldGenHelper.setOre(world, mut, existing, ore.getMaterial(), Data.ORE_SMALL);
            }
        }
        return true;
    }
}
