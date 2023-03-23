package com.games.theory.utils;

import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FifoQueue extends LinkedList<Node> implements IFifoQueue {
    private final int limit;
    private final String pattern;

    @Override
    public void addFirst(Node node) {
        super.addFirst(node);
        while (size() > limit) {
            super.removeLast();
        }
    }

    @Override
    public String isAllEqual(String pattern) {
        String toCompare = ((com.games.theory.tictactoe.model.Node) this.get(0).getUserData()).getMarkName();
        int freeNodeChecker = 0;
        if (!toCompare.isEmpty()) {
            for (Node element:this) {
                if(!((com.games.theory.tictactoe.model.Node) element.getUserData()).getMarkName().equals(toCompare)) return null;
                if (!((com.games.theory.tictactoe.model.Node) element.getUserData()).isChecked()) freeNodeChecker++;
            }
            if (freeNodeChecker == 0) {
                return null;
            }
            for (Node element:this) {
                ((com.games.theory.tictactoe.model.Node) element.getUserData()).setChecked(true);
                StackPane pane = (StackPane) element;
                pane.getChildren().add(createLine(pane));
            }
            return ((com.games.theory.tictactoe.model.Node) this.get(0).getUserData()).getMarkName();
        }
        return null;
    }

    @Override
    public boolean isFull() {
        return super.size() >= this.limit;
    }

    @Override
    public void print() {
        for (var item:this) {
            var node = ((com.games.theory.tictactoe.model.Node)item.getUserData());
            log.debug("pattern: {} - col: {} - row: {} - mark: {}", pattern, node.getColIndex(), node.getRowIndex(), node.getMarkName());
        }
        log.debug("END\n");
    }

    private Line createLine(StackPane pane) {
        switch (pattern) {
            case "column":
                return new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX() +
                                   pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            case "row":
                return new Line(pane.getLayoutX(), pane.getLayoutY() - pane.getHeight(), pane.getLayoutX() +
                                pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            case "diagonal-to-right":
                return new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX(),
                               pane.getLayoutY() - pane.getHeight());
            case "diagonal-to-left":
                return new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
                                pane.getWidth(),pane.getLayoutY() - pane.getHeight());
            default:
                return new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
                                pane.getWidth() / 2, pane.getLayoutY() - pane.getHeight() / 2);
        }
    }
}
