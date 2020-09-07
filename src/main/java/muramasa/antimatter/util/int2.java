package muramasa.antimatter.util;

public class int2 {

    private int x;
    private int y;

    public int2() {

    }

    public int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof int2)) return false;
        int2 other = (int2) o;
        return getX() == other.getX() && getY() == other.getY();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
