package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ModList;

public class ScreenMachine<T extends ContainerMachine> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected T container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getMachineTier(), "machine");
    }

    protected void drawTitle(MatrixStack matrixStack, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getTile().getMachineType().getGui().getButtonLocation();
        for (ButtonData button : container.getTile().getMachineType().getGui().getButtons()) {
            addButton(new ButtonWidget(loc, guiLeft + button.getX(), guiTop + button.getY(), button.getW(), button.getH(), button.getBody(), button.getOverlay(), button.getText(), b -> {
                int shiftHold = 0; //TODO: FIX
                //int shiftHold = playerInventory.player.isShiftKeyDown() ? 1 : 0;
                container.getTile().onGuiEvent(GuiEvent.BUTTON_PRESSED, button.getId(), shiftHold);
                Antimatter.NETWORK.sendToServer(new GuiEventPacket(GuiEvent.BUTTON_PRESSED, container.getTile().getPos(), container.getTile().getDimensionKey(), button.getId(), shiftHold));
            }));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        drawTexture(matrixStack, gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawTitle(matrixStack, mouseX, mouseY);
        if (container.getTile().has(MachineFlag.RECIPE)) {
            drawTooltipInArea(matrixStack, "Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
        if (container.getTile().has(MachineFlag.FLUID)) {
            //TODO
            //drawContainedFluids(mouseX, mouseY);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        container.getTile().drawInfo(matrixStack, Minecraft.getInstance().fontRenderer, guiLeft, guiTop);
    }

    protected void drawProgress(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int progressTime = (int)(20 * container.getTile().getClientProgress());
        drawTexture(matrixStack, gui, guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!ModList.get().isLoaded("jei") || !container.getTile().has(MachineFlag.RECIPE)) return false;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            //TODO
            //GregTechJEIPlugin.showCategory(container.getTile().getMachineType());
            return true;
        }
        return false;
    }
}
