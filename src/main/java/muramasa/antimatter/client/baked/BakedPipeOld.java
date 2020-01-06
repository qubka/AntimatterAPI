package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.cover.Cover;
import muramasa.gtu.data.Textures;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.QuadLayer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.Direction.*;

public class BakedPipeOld implements IDynamicBakedModel {

    protected static TextureAtlasSprite PARTICLE = Textures.PIPE.getSprite();

    public static Int2ObjectOpenHashMap<List<BakedQuad>> CACHE = new Int2ObjectOpenHashMap<>();
    public static int[][] CONFIG = new int[64][];
    public static IBakedModel[][] BAKED;
    public static IBakedModel[] PIPE_EXTRA;

    static {
        //Default Shape (0 Connections)
        CONFIG[0] = new int[]{0};

        //Single Shapes (1 Connections)
        CONFIG[1] = new int[]{1, DOWN.getIndex()};
        CONFIG[2] = new int[]{1, UP.getIndex()};
        CONFIG[4] = new int[]{1};
        CONFIG[8] = new int[]{1, SOUTH.getIndex()};
        CONFIG[16] = new int[]{1, WEST.getIndex()};
        CONFIG[32] = new int[]{1, EAST.getIndex()};

        //Line Shapes (2 Connections)
        CONFIG[3] = new int[]{2, UP.getIndex()};
        CONFIG[12] = new int[]{2};
        CONFIG[48] = new int[]{2, WEST.getIndex()};

        //Elbow Shapes (2 Connections)
        CONFIG[5] = new int[]{3, WEST.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[6] = new int[]{3, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[9] = new int[]{3, EAST.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[10] = new int[]{3, EAST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[17] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[18] = new int[]{3, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[20] = new int[]{3, WEST.getIndex()};
        CONFIG[24] = new int[]{3, SOUTH.getIndex()};
        CONFIG[33] = new int[]{3, NORTH.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[34] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[36] = new int[]{3};
        CONFIG[40] = new int[]{3, EAST.getIndex()};

        //Side Shapes (3 Connections)
        CONFIG[7] = new int[]{4, SOUTH.getIndex(), UP.getIndex()};
        CONFIG[11] = new int[]{4, NORTH.getIndex(), UP.getIndex()};
        CONFIG[13] = new int[]{4, DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[14] = new int[]{4};
        CONFIG[19] = new int[]{4, EAST.getIndex(), UP.getIndex()};
        CONFIG[28] = new int[]{4, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[35] = new int[]{4, WEST.getIndex(), UP.getIndex()};
        CONFIG[44] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[49] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[50] = new int[]{4, EAST.getIndex()};
        CONFIG[52] = new int[]{4, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[56] = new int[]{4, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};

        //Corner Shapes (3 Connections)
        CONFIG[21] = new int[]{5, WEST.getIndex(), DOWN.getIndex()};
        CONFIG[22] = new int[]{5, WEST.getIndex()};
        CONFIG[25] = new int[]{5, SOUTH.getIndex(), DOWN.getIndex()};
        CONFIG[26] = new int[]{5, SOUTH.getIndex()};
        CONFIG[41] = new int[]{5, EAST.getIndex(), DOWN.getIndex()};
        CONFIG[42] = new int[]{5, EAST.getIndex()};
        CONFIG[37] = new int[]{5, NORTH.getIndex(), DOWN.getIndex()};
        CONFIG[38] = new int[]{5};

        //Arrow Shapes (4 Connections)
        CONFIG[23] = new int[]{6, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[27] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[29] = new int[]{6, WEST.getIndex(), DOWN.getIndex()};
        CONFIG[30] = new int[]{6, WEST.getIndex()};
        CONFIG[39] = new int[]{6, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[43] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[45] = new int[]{6, EAST.getIndex(), DOWN.getIndex()};
        CONFIG[46] = new int[]{6, EAST.getIndex()};
        CONFIG[53] = new int[]{6, DOWN.getIndex()};
        CONFIG[54] = new int[]{6};
        CONFIG[57] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex()};
        CONFIG[58] = new int[]{6, SOUTH.getIndex()};

        //Cross Shapes (4 Connections)
        CONFIG[15] = new int[]{7, WEST.getIndex(), UP.getIndex()};
        CONFIG[51] = new int[]{7, UP.getIndex()};
        CONFIG[60] = new int[]{7};

        //Five Shapes (5 Connections)
        CONFIG[31] = new int[]{8, EAST.getIndex(), UP.getIndex()};
        CONFIG[47] = new int[]{8, WEST.getIndex(), UP.getIndex()};
        CONFIG[55] = new int[]{8, SOUTH.getIndex(), UP.getIndex()};
        CONFIG[59] = new int[]{8, NORTH.getIndex(), UP.getIndex()};
        CONFIG[61] = new int[]{8, DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[62] = new int[]{8};

        //All Shapes (6 Connections)
        CONFIG[63] = new int[]{9};
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
//            data.setData(GTProperties.PIPE_CONNECTIONS, );
//        }
        return data;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (!(data.hasProperty(AntimatterProperties.PIPE_SIZE) || data.hasProperty(AntimatterProperties.PIPE_CONNECTIONS))) return Collections.emptyList();

        int size = data.getData(AntimatterProperties.PIPE_SIZE).ordinal();
        int connections = data.getData(AntimatterProperties.PIPE_CONNECTIONS);
        //TextureData data = exState.getValue(GTProperties.TEXTURE);
        //Cover[] covers = exState.getValue(GTProperties.COVER);

        //List<BakedQuad> quads = CACHE.get((size * 100) + connections);
        List<BakedQuad> quads = null;
        if (quads == null) {
            int[] config = connections > 63 ? CONFIG[connections - 64] : CONFIG[connections];
            quads = new ArrayList<>(BAKED[size][config[0]].getQuads(state, side, rand));
            if (connections > 63) quads = ModelUtils.remove(quads, QuadLayer.OVERLAY);
            if (config.length > 1) quads = ModelUtils.trans(quads, 1, config);
            //ModelUtils.tex(quads, QuadLayer.BASE, QuadLayer.OVERLAY, data.getBase(0));
            //ModelUtils.tex(quads, QuadLayer.EXTRA, data.getOverlay(size));

//            if (covers != null) {
//                for (int s = 0; s < 6; s++) {
//                    if (!covers[s].isEmpty()) {
//                        //TODO get Tier from cover instance when all covers have a tier member
//                        quads.addAll(covers[s].onRender(this, ModelUtils.tex(getCovers(covers[s], s, state), QuadLayer.COVER_BASE, Tier.LV.getBaseTexture()), s));
//                    }
//                }
//            }
        }
        //CACHE.put((size * 100) + connections, quads);
        return quads;
    }

    public List<BakedQuad> getCovers(Cover cover, int s, BlockState state) {
//        List<BakedQuad> quads = ModelUtils.trans(BakedMachine.COVERS.get(cover.getId()).getQuads(state, null, -1), s);
//        quads.addAll(PIPE_EXTRA[s].getQuads(state, null, -1));
//        return quads;
        return Collections.emptyList();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, ModelUtils.getBlockTransform(cameraTransformType));
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return PARTICLE;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}