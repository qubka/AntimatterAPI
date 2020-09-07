package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Created By Muramasa -  https://github.com/Muramasa-
 * Allows easily stepping in directions given a Direction
 */
public class int3 extends BlockPos.Mutable {

    private Direction side = Direction.NORTH; //Used for moving in a direction

    public int3() {
    }

    public int3(int x, int y, int z) {
        super(x, y, z);
    }

    public int3(BlockPos pos, Direction side) {
        super(pos.getX(), pos.getY(), pos.getZ());
        this.side = side;
    }

    public int3 set(BlockPos pos) {
        return (int3) setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public void set(Direction side) {
        this.side = side;
    }

    public int3 right(int n) {
        return offset(n, getSide().rotateY());
    }

    public int3 left(int n) {
        return offset(n, getSide().rotateYCCW());
    }

    public int3 forward(int n) {
        return offset(n, getSide());
    }

    public int3 back(int n) {
        return offset(n, getSide().getOpposite());
    }

    @Nonnull
    public int3 up(int n) {
        return offset(n, Direction.UP);
    }

    @Nonnull
    public int3 down(int n) {
        return offset(n, Direction.DOWN);
    }

    @Nonnull
    @Override
    public BlockPos offset(Direction side) {
        return offset(1, side);
    }

    @Nonnull
    @Override
    public BlockPos offset(Direction side, int n) {
        return offset(n, side);
    }

    public int3 offset(int n, Direction side) {
        if (n == 0 || side == null) return this;
        setPos(getX() + side.getXOffset() * n, getY() + side.getYOffset() * n, getZ() + side.getZOffset() * n);
        return this;
    }

    public int3 offset(int2 n, Dir... directions) {
        if (getSide() != null && directions.length >= 2) {
            offset(n.getX(), directions[0].getRotatedFacing(getSide()));
            offset(n.getY(), directions[1].getRotatedFacing(getSide()));
        }
        return this;
    }

    public int3 offset(int3 n, Dir... directions) {
        if (getSide() != null && directions.length >= 3) {
            offset(n.getX(), directions[0].getRotatedFacing(getSide()));
            offset(n.getY(), directions[1].getRotatedFacing(getSide()));
            offset(n.getZ(), directions[2].getRotatedFacing(getSide()));
        }
        return this;
    }

    public int3 offset(int3 n, Direction... facings) {
        if (facings.length >= 3) {
            offset(n.getX(), facings[0]);
            offset(n.getY(), facings[1]);
            offset(n.getZ(), facings[2]);
        }
        return this;
    }

    @Nonnull
    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

    public Direction getSide() {
        return side;
    }
}
