package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class AntimatterContainerScreen<T extends Container> extends ContainerScreen<T> implements IHasContainer<T> {

    public AntimatterContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    public void drawTexture(MatrixStack matrixStack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(matrixStack, left, top, x, y, sizeX, sizeY);
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(MatrixStack matrixStack, String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        // TODO: Fix that
        //List<? extends IReorderingProcessor> list = new ObjectArrayList<>();
        //list.add(line);
        //drawTooltipInArea(matrixStack, list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(MatrixStack matrixStack, List<? extends IReorderingProcessor> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            renderTooltip(matrixStack, lines, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }

//    public void drawContainedFluids(int mouseX, int mouseY) {
//        tile.fluidHandler.ifPresent(h -> {
//            FluidStack[] fluids;
//            List<SlotData> slots;
//
//            //TODO this should use getInputsRaw to preserve ordering
//            fluids = h.getInputs();
//            slots = tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier());
//            if (fluids != null) {
//                for (int i = 0; i < fluids.length; i++) {
//                    if (i >= slots.size()) continue;
//                    RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
//                    drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
//                }
//            }
//            fluids = h.getOutputs();
//            slots = tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier());
//            if (fluids != null) {
//                for (int i = 0; i < fluids.length; i++) {
//                    if (i >= slots.size()) continue;
//                    RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
//                    drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
//                }
//            }
//        });
//    }
}
