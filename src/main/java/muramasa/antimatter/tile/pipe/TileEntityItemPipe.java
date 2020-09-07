package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.Tesseract;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.api.item.IItemPipe;
import tesseract.util.Dir;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe, ITickHost {

    private ITickingController controller;

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (isServerSide()) Tesseract.ITEM.registerConnector(getDimensionKey(), pos.toLong(), this); // this is connector class
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ITEM.remove(getDimensionKey(), pos.toLong());
            Tesseract.ITEM.registerConnector(getDimensionKey(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getDimensionKey(), pos.toLong());
        super.onRemove();
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) controller.tick();
    }

    @Override
    public int getCapacity() {
        return ((ItemPipe<?>)getPipeType()).getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController) {
            controller = newController;
        }
    }
}
