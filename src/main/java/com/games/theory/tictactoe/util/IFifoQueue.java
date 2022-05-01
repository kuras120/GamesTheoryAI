package com.games.theory.tictactoe.util;

import javafx.scene.Node;

public interface IFifoQueue {
    void addFirst(Node node);
    String isAllEqual(String pattern);
    void clear();
    boolean isFull();
    void print();
}
