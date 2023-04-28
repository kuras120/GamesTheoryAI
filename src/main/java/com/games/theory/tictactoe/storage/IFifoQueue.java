package com.games.theory.tictactoe.storage;

import javafx.scene.Node;

public interface IFifoQueue {
    void addFirst(Node node);
    String isAllEqual();
    void clear();
    boolean isFull();
    void print();
}
