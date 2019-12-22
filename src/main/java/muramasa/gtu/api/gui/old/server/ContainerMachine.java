//package muramasa.gtu.api.gui.server;
//
//import muramasa.gtu.api.gui.GuiEvent;
//import muramasa.gtu.api.gui.SlotData;
//import muramasa.gtu.api.gui.slot.SlotInput;
//import muramasa.gtu.api.gui.slot.SlotOutput;
//import muramasa.gtu.api.machines.MachineState;
//import muramasa.gtu.api.network.GregTechNetwork;
//import muramasa.gtu.api.tileentities.TileEntityMachine;
//import net.minecraft.inventory.IInventory;
//
//import javax.annotation.Nullable;
//
//public class ContainerMachine extends ContainerBase {
//
//    protected TileEntityMachine tile;
//    private int lastState = -1;
//
//    public ContainerMachine(TileEntityMachine tile, @Nullable IInventory playerInv) {
//        super(tile.getType().getGui().getSlots(tile.getTier()).size(), playerInv);
//        addSlots(tile);
//        if (tile.getType().getGui().enablePlayerSlots()) addPlayerInventorySlots();
//        this.tile = tile;
//    }
//
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
//
//    protected void addSlots(TileEntityMachine tile) {
//        tile.itemHandler.ifPresent(h -> {
//            int inputIndex = 0, outputIndex = 0, cellIndex = 0;
//            for (SlotData slot : tile.getType().getGui().getSlots(tile.getTier())) {
//                switch (slot.type) {
//                    case IT_IN:
//                        addSlotToContainer(new SlotInput(h.getInputHandler(), inputIndex++, slot.x, slot.y));
//                        break;
//                    case IT_OUT:
//                        addSlotToContainer(new SlotOutput(h.getOutputHandler(), outputIndex++, slot.x, slot.y));
//                        break;
//                    case CELL_IN:
//                        addSlotToContainer(new SlotInput(h.getCellHandler(), cellIndex++, slot.x, slot.y));
//                        break;
//                    case CELL_OUT:
//                        addSlotToContainer(new SlotOutput(h.getCellHandler(), cellIndex++, slot.x, slot.y));
//                        break;
//                }
//            }
//        });
//    }
//}