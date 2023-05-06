package com.games.theory.tictactoe.storage;

import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
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

    @Override
    public String isAllEqual() {
        var firstNode = getUserNode(this.get(0).getUserData());
        var firstNodeMark = firstNode.getMarkName();
        int freeNodeChecker = 0;
        if (!firstNodeMark.isEmpty()) {
            for (Node element:this) {
                if(!getUserNode(element.getUserData()).getMarkName().equals(firstNodeMark)) return null;
                if (!getUserNode(element.getUserData()).isChecked()) freeNodeChecker++;
            }
            if (freeNodeChecker == 0) {
                return null;
            }
            this.forEach(element -> {
                getUserNode(element.getUserData()).setChecked(true);
                StackPane pane = (StackPane) element;
                pane.getChildren().add(LinePredictor.createLine(pane, getUserNode(this.get(1).getUserData()), firstNode));
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
    public void print(String text) {
        log.debug("{}", text);
        this.forEach(element -> {
            var node = getUserNode(element.getUserData());
            log.debug("col: {} - row: {} - mark: {}", node.getColIndex(), node.getRowIndex(), node.getMarkName());
        });
        log.debug("END\n");
    }

    private com.games.theory.tictactoe.model.Node getUserNode(Object userData) {
        return (com.games.theory.tictactoe.model.Node) userData;
    }
}
