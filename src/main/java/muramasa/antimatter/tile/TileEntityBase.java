package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void remove() {
        onRemove();
        super.remove();
    }

    public void onRemove() {
        //NOOP
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
    }

    public RegistryKey<World> getDimensionKey() {
        return world.getDimensionKey();
    }

    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getName());
        return info;
    }
}
