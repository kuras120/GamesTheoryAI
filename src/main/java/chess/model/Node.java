package chess.model;

public class Node {

    private Chessman chessman;
    private boolean selected;

    public Node() {}

    public Node(Chessman chessman, boolean selected) {
        this.chessman = chessman;
        this.selected = selected;
    }

    public Chessman getChessman() {
        return chessman;
    }

    public void setChessman(Chessman chessman) {
        this.chessman = chessman;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
