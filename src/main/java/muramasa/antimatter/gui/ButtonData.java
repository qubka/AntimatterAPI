package muramasa.antimatter.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ButtonData {

    private int id;
    private int x;
    private int y;
    private int w;
    private int h;
    private ButtonBody body;
    private ButtonOverlay overlay;
    private ITextComponent text = new StringTextComponent("");

    public ButtonData(int id, int x, int y, int w, int h, ButtonBody body) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.body = body;
    }

    public ButtonData(int id, int x, int y, int w, int h, ButtonBody body, ITextComponent text) {
        this(id, x, y, w, h, body);
        this.text = text;
    }

    public ButtonData(int id, int x, int y, int w, int h, ButtonBody body, ButtonOverlay overlay) {
        this(id, x, y, w, h, body);
        this.overlay = overlay;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public ButtonBody getBody() {
        return body;
    }

    public ButtonOverlay getOverlay() {
        return overlay;
    }

    public ITextComponent getText() {
        return text;
    }
}
