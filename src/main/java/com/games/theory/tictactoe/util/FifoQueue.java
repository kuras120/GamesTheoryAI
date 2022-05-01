package com.games.theory.tictactoe.util;

import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FifoQueue extends LinkedList<Node> implements IFifoQueue {

    private final int limit;

    public FifoQueue(int limit) {
        this.limit = limit;
    }

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
        if (!"".equals(toCompare)) {
            for(Node element:this) {
//                System.out.println(((com.games.theory.tictactoe.model.Node) element.getUserData()).getMarkName());
                if(!((com.games.theory.tictactoe.model.Node) element.getUserData()).getMarkName().equals(toCompare)) return null;
                if (!((com.games.theory.tictactoe.model.Node) element.getUserData()).isChecked()) freeNodeChecker++;
            }
//            System.out.println("--------------------------------------------------------------------------------");
            if (freeNodeChecker == 0) {
//                System.out.println("BLEH");
                return null;
            }
            for (Node element:this) {
                ((com.games.theory.tictactoe.model.Node) element.getUserData()).setChecked(true);
                StackPane pane = (StackPane) element;
                pane.getChildren().add(createLine(pattern, pane));
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
        for(var item:this) {
            var node = ((com.games.theory.tictactoe.model.Node)item.getUserData());
            log.info("{} {} {}", node.getColIndex(), node.getRowIndex(), node.getMarkName());
        }
        log.info("\n");
    }

    private Line createLine(String pattern, StackPane pane) {
        switch(pattern) {
            case "column":
                return new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX() +
                                   pane.getWidth(), pane.getLayoutY() - pane.getHeight());
            case "row":
                return new Line(pane.getLayoutX(), pane.getLayoutY() - pane.getHeight(), pane.getLayoutX() +
                                pane.getWidth(),pane.getLayoutY() - pane.getHeight());
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
