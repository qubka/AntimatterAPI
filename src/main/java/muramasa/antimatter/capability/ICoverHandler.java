package muramasa.antimatter.capability;

import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ICoverHandler {

    void onUpdate();

    boolean onPlace(Direction side, Cover cover);

    void onRemove();

    Cover getCover(Direction side);

    boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type);

    Cover[] getAll();

    boolean hasCover(Direction side, Cover cover);

    boolean isValid(Direction side, Cover existing, Cover replacement);

    Direction getTileFacing();

    TileEntity getTile();
}
