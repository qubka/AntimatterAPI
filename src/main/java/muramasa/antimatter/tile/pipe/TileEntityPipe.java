package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCapabilityHolder;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.capability.pipe.PipeInteractHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.capability.CapabilitySide.SERVER_AND_CLIENT;

public class TileEntityPipe extends TileEntityTickable {

    /** Pipe Data **/
    protected PipeType<?> type;
    protected PipeSize size;

    /** Capabilities **/
    public PipeCapabilityHolder<PipeCoverHandler> coverHandler = new PipeCapabilityHolder<>(this, SERVER_AND_CLIENT);
    public PipeCapabilityHolder<PipeInteractHandler> interactHandler = new PipeCapabilityHolder<>(this, SERVER_AND_CLIENT);

    protected byte connection;

    public TileEntityPipe(TileEntityType<?> tileType) {
        super(tileType);
    }

    public TileEntityPipe(PipeType<?> type) {
        this(type.getTileType());
        this.type = type;
        coverHandler.init(PipeCoverHandler::new);
        interactHandler.init(PipeInteractHandler::new);
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        interactHandler.ifPresent(PipeInteractHandler::onRemove);
    }

    public PipeType<?> getPipeType() {
        return type != null ? type : (type = ((BlockPipe<?>) getBlockState().getBlock()).getType());
    }

    public PipeSize getPipeSize() { //TODO need to store? when getBlockState is cached?
        return size != null ? size : (size = ((BlockPipe<?>) getBlockState().getBlock()).getSize());
    }

    public byte getConnection() {
        return connection;
    }

    public void setConnection(Direction side) {
        connection = Connectivity.set(connection, side.getIndex());
        refreshConnection();
    }

    public void toggleConnection(Direction side) {
        connection = Connectivity.toggle(connection, side.getIndex());
        refreshConnection();
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
        refreshConnection();
    }

    public void changeConnection(Direction side) {
        interactHandler.ifPresent(h -> h.onChange(side));
    }

    public void refreshConnection() {
        Utils.markTileForRenderUpdate(this);
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public Cover[] getValidCovers() {
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverInstance<?>[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new CoverInstance[0]);
    }

    public CoverInstance<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return getCapability(cap, /*allow null here?*/null);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.COVERABLE) {
            return coverHandler.map(h -> !h.get(side).isEmpty()).orElse(false) ? LazyOptional.of(() -> coverHandler.get()).cast() : super.getCapability(cap, side);
        }
        if (cap == AntimatterCaps.INTERACTABLE) {
            return interactHandler.<LazyOptional<T>>map(h -> LazyOptional.of(() -> h).cast()).orElseGet(() -> super.getCapability(cap));
        }
        return LazyOptional.empty();
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_CONNECTIVITY)) connection = tag.getByte(Ref.KEY_PIPE_TILE_CONNECTIVITY);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_COVER)));
        if (tag.contains(Ref.KEY_PIPE_TILE_CONFIG)) interactHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_PIPE_TILE_CONFIG)));
        //TODO refreshConnection(); causes crash as the world object has not yet been assigned
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        tag.putByte(Ref.KEY_PIPE_TILE_CONNECTIVITY, connection);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serialize()));
        interactHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_CONFIG, h.serialize()));
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
        return info;
    }
}
