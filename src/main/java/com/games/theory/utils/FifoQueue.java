package com.games.theory.utils;

import java.util.LinkedList;

import io.github.palexdev.materialfx.utils.StringUtils;
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

    @Override
    public void addFirst(Node node) {
        super.addFirst(node);
        while (size() > limit) {
            super.removeLast();
        }
    }

    // TODO which line to draw detection
    @Override
    public String isAllEqual() {
        String toCompare = ((com.games.theory.tictactoe.model.Node) this.get(0).getUserData()).getMarkName();
        int freeNodeChecker = 0;
        if (!toCompare.isEmpty()) {
            for (Node element:this) {
                if(!getUserNode(element.getUserData()).getMarkName().equals(toCompare)) return null;
                if (!getUserNode(element.getUserData()).isChecked()) freeNodeChecker++;
            }
            if (freeNodeChecker == 0) {
                return null;
            }
            this.forEach(element -> {
                getUserNode(element.getUserData()).setChecked(true);
                StackPane pane = (StackPane) element;
                pane.getChildren().add(createLine(pane));
            });
            return getUserNode(this.get(0).getUserData()).getMarkName();
        }
        return null;
    }

    @Override
    public boolean isFull() {
        return super.size() >= this.limit;
    }

    @Override
    public void print() {
        this.forEach(element -> {
            var node = getUserNode(element.getUserData());
            log.debug("col: {} - row: {} - mark: {}", node.getColIndex(), node.getRowIndex(), node.getMarkName());
        });
        log.debug("END\n");
    }

    private com.games.theory.tictactoe.model.Node getUserNode(Object userData) {
        return (com.games.theory.tictactoe.model.Node) userData;
    }

    private Line createLine(StackPane pane) {
        return switch (predictPattern()) {
            case "column" -> new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX() +
                pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            case "row" -> new Line(pane.getLayoutX(), pane.getLayoutY() - pane.getHeight(), pane.getLayoutX() +
                pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            case "diagonal-to-right" ->
                new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX(),
                    pane.getLayoutY() - pane.getHeight());
            case "diagonal-to-left" -> new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
                pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            default -> new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
                pane.getWidth() / 2, pane.getLayoutY() - pane.getHeight() / 2);
        };
    }

    private String predictPattern() {
        if (this.size() > 1) {
            var userData1 = getUserNode(this.get(0).getUserData());
            var userData2 = getUserNode(this.get(1).getUserData());
            int rowDifference = userData2.getRowIndex() - userData1.getRowIndex();
            int columnDifference = userData2.getColIndex() - userData1.getColIndex();
            String difference = String.valueOf(columnDifference) + rowDifference;
            return switch (difference) {
                case "10" -> "column";
                case "01" -> "row";
                case "11" -> "diagonal-to-right";
                case "-11" -> "diagonal-to-left";
                default -> throw new IllegalStateException("Unexpected value: " + difference);
            };
        }
        return StringUtils.EMPTY;
    }
}
