package tictactoe.util;

import javafx.scene.Node;

public interface IFifoQueue {
    void addFirst(Node node);
    boolean isAllEqual(String pattern);
    void clear();
    boolean isFull();
}
