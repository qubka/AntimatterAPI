package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.slot.SlotInput;
import muramasa.antimatter.gui.slot.SlotOutput;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

import java.util.List;

public abstract class ContainerMachine extends AntimatterContainer {

    protected TileEntityMachine tile;
    private int lastState = -1;

    public ContainerMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandlerMachine menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getGui().getSlots(tile.getMachineTier()).size());
        addSlots(tile);
        if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        this.tile = tile;
    }

    public TileEntityMachine getTile() {
        return tile;
    }

//    @Override
//    public void detectAndSendChanges() {
//        super.detectAndSendChanges();
//        int curState = tile.getMachineState().ordinal();
//        if (Math.abs(curState - lastState) >= GuiEvent.MACHINE_STATE.getUpdateThreshold()) {
//            listeners.forEach(l -> l.sendWindowProperty(this, GuiEvent.MACHINE_STATE.ordinal(), curState));
//            lastState = curState;
//        }
//        tile.fluidHandler.ifPresent(h -> {
//            if ((h.getInputWrapper() != null && h.getInputWrapper().dirty) || (h.getOutputWrapper() != null && h.getOutputWrapper().dirty)) {
//                if (h.getInputWrapper() != null) h.getInputWrapper().dirty = false;
//                if (h.getOutputWrapper() != null) h.getOutputWrapper().dirty = false;
//                GregTechNetwork.syncMachineTanks(tile);
//            }
//        });
//    }
//
//    @Override
//    public void updateProgressBar(int id, int data) {
//        super.updateProgressBar(id, data);
//        if (id == GuiEvent.MACHINE_STATE.ordinal()) {
//            tile.setMachineState(MachineState.VALUES[data]);
//        }
//    }

    protected void addSlots(TileEntityMachine tile) {
        tile.itemHandler.ifPresent(h -> {
            int inputIndex = 0, outputIndex = 0, cellIndex = 0;
            List<SlotData> lst = tile.getMachineType().getGui().getSlots(tile.getMachineTier());
            for (SlotData slot : lst) {
                switch (slot.type) {
                    case IT_IN:
                        addSlot(new SlotInput(h.getInputWrapper(), inputIndex++, slot.x, slot.y));
                        break;
                    case IT_OUT:
                        addSlot(new SlotOutput(h.getOutputWrapper(), outputIndex++, slot.x, slot.y));
                        break;
                    case CELL_IN:
                        addSlot(new SlotInput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                    case CELL_OUT:
                        addSlot(new SlotOutput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                }
            }
        });
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
