package com.games.theory.chess.model;

import javafx.scene.layout.GridPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    private Chessman chessman;
    private boolean selected;
    private int coordX;
    private int coordY;

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
}
