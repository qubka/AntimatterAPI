package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.machine.Tier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
//A screen showing the GUI for the cover.
public class ScreenCover<T extends ContainerCover> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected ContainerCover container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenCover(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        if (container.getCover().getCover() instanceof CoverTiered) {
            this.gui = container.getCover().getCover().getGui().getTexture((((CoverTiered)container.getCover().getCover()).getTier()),"cover");
        } else {
            this.gui = container.getCover().getCover().getGui().getTexture(Tier.LV,"cover");
        }
    }

    protected void drawTitle(MatrixStack matrixStack, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawTexture(matrixStack, gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
