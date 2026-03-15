package ds.agh.chatapp.common.model;

public enum Color {
    RED, GREEN, BLUE, YELLOW, ORANGE, PURPLE, CYAN, MAGENTA, WHITE;

    public String toHex() {
        return switch (this) {
            case RED -> "#FF0000";
            case GREEN -> "#00FF00";
            case BLUE -> "#0000FF";
            case YELLOW -> "#FFFF00";
            case ORANGE -> "#FFA500";
            case PURPLE -> "#800080";
            case CYAN -> "#00FFFF";
            case MAGENTA -> "#FF00FF";
            default -> "#FFFFFF";
        };
    }
}
