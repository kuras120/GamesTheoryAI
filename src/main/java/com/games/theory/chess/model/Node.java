package com.games.theory.chess.model;

import javafx.scene.layout.GridPane;

public class Node {

    private Chessman chessman;
    private boolean selected;
    private int coordX;
    private int coordY;

    public Node() {}

    public Node(Chessman chessman, boolean selected, int coordX, int coordY) {
        this.chessman = chessman;
        this.selected = selected;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public javafx.scene.Node getBackgroundNode(GridPane table, String clazz, Integer coordX, Integer coordY) {
        if (coordX == null || coordY == null) {
            coordX = this.coordX;
            coordY = this.coordY;
        }
        var children = table.getChildren();
        if (clazz != null) {
            children = children.filtered(p -> p.getStyleClass().contains(clazz));
        }
        return children.get(coordX * 8 + coordY);
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

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }
}
