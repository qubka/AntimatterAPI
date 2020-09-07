package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

public interface IInfoRenderer {

    default void drawInfo(MatrixStack matrixStack, FontRenderer renderer, int left, int top) {
        //NOOP
    }
}
