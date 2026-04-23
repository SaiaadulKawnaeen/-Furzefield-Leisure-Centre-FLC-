package flc;

public enum TimeSlot {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening");

    private final String label;

    TimeSlot(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}