package muramasa.antimatter.machine.event;

public enum GuiEvent implements IMachineEvent {

    PROGRESS(1.0f),
    MACHINE_STATE(1.0f),
    MULTI_ACTIVATE(1.0f);

    public static GuiEvent[] VALUES;

    static {
        VALUES = values();
    }

    private float updateThreshold;

    GuiEvent(float updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    public float getUpdateThreshold() {
        return updateThreshold;
    }
}
