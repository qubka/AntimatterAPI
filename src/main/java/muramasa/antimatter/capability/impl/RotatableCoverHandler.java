package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public class RotatableCoverHandler extends CoverHandler {

    public RotatableCoverHandler(TileEntity tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile(), Utils.rotateFacingAlt(Ref.DIRECTIONS[i], getTileFacing()));
        }
    }

    @Override
    public boolean onPlace(Direction side, @Nonnull Cover cover) {
        return super.onPlace(Utils.rotateFacing(side, getTileFacing()), cover);
    }

    @Override
    public Cover getCover(Direction side) {
        return super.getCover(Utils.rotateFacing(side, getTileFacing()));
    }
}
