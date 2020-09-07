package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
//import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.util.Utils.*;

//TODO: REWORK CLASS
public class AntimatterItemTagProvider extends ForgeItemTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen, new BlockTagsProvider(gen));
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        this.tagToBuilder.clear();
        registerTags();
        /*TagCollection<Item> tags = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Item>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().getId(), Map.Entry::getValue));
        tags.registerAll(map);
        tags.getTagMap().forEach((k, v) -> DynamicResourcePack.addTag("items", k, v.serialize(this.registry::getKey)));
        this.setCollection(tags);*/
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        ITag.INamedTag<Block> blockTag = BLOCK.getTag(), frameTag = FRAME.getTag();
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        AntimatterAPI.all(BlockOre.class, o -> {
            if (o.getOreType() == ORE_SMALL) return;
            String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
            this.copy(getForgeBlockTag(name), getForgeItemTag(name));
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            String name = domain + "blocks/" + s.getId();
            this.copy(getBlockTag(name), getItemTag(name));
        });
        // AntimatterAPI.all(BlockOreStone.class, domain, s -> {
            // String id = s.getId().replaceAll("_stone_", "s/");
            // this.copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
        // });
        AntimatterAPI.all(BlockStorage.class, domain, storage -> {
            MaterialType<?> type = storage.getType();
            String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
            this.copy(getForgeBlockTag(name), getForgeItemTag(name));
        });
        AntimatterAPI.all(MaterialItem.class, domain, item -> {
            this.getOrCreateBuilder(item.getType().getTag()).add(item).replace(replace);
            String name = String.join("", getConventionalMaterialType(item.getType()), "/", item.getMaterial().getId());
            this.getOrCreateBuilder(getForgeItemTag(name)).add(item).replace(replace).replace(replace);
            // TODO: What is BEACON_PAYMENT ?
            ///if (item.getType() == INGOT || item.getType() == GEM) this.getOrCreateBuilder(Tags.Items.BEACON_PAYMENT).add(item);
        });
        AntimatterAPI.all(IAntimatterTool.class, domain, tool -> {
            this.getOrCreateBuilder(tool.getType().getTag()).add(tool.getItem()).replace(replace);
            this.getOrCreateBuilder(tool.getType().getForgeTag()).add(tool.getItem()).replace(replace);
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}