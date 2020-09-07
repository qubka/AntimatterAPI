package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.MachineFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ScreenHatch<T extends ContainerMachine> extends ScreenMachine<T> {

    public ScreenHatch(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        gui = new ResourceLocation(Ref.ID, "textures/gui/machine/hatch.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        List<SlotData> list = container.getTile().getMachineType().getGui().getSlots(container.getTile().getMachineTier());
        for (SlotData slot : list) {
            if (slot.getType() == SlotType.IT_IN || slot.getType() == SlotType.IT_OUT) {
                drawTexture(matrixStack, gui, guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, xSize, 0, 18, 18);
            } else if (slot.getType() == SlotType.FL_IN || slot.getType() == SlotType.FL_OUT) {
                drawTexture(matrixStack, gui, guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, xSize, 18, 18, 18);
            }
        }
        if (container.getTile().getMachineType().has(MachineFlag.FLUID)) {
            drawTexture(matrixStack, gui, guiLeft + 7, guiTop + 15, xSize, 36, 18, 54);
        }
    }
}
