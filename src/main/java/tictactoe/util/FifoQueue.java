package tictactoe.util;

import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

public class FifoQueue extends LinkedList<Node> implements IFifoQueue {

    private int limit;
    private String markName;

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
        String toCompare = ((tictactoe.model.Node) this.get(0).getUserData()).getMarkName();
        if (!toCompare.equals("")) {
            for(Node element:this) {
                if(!((tictactoe.model.Node) element.getUserData()).getMarkName().equals(toCompare)) return null;
            };
            for (Node element:this) {
                StackPane pane = (StackPane) element;
                pane.getChildren().add(createLine(pattern, pane));
            }
            return ((tictactoe.model.Node) this.get(0).getUserData()).getMarkName();
        }
        return null;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean isFull() {
        return super.size() == this.limit;
    }

    @Override
    public void print() {
        for(var item:this) {
            var node = ((tictactoe.model.Node)item.getUserData());
            System.out.println(node.getColIndex() + " " + node.getRowIndex() + " " + node.getMarkName());
        }
        System.out.println("\n");
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
