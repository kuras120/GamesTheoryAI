package com.games.theory.tictactoe.utils;

import javafx.scene.Node;

public interface IFifoQueue {
    void addFirst(Node node);
    String isAllEqual(String pattern);
    void clear();
    boolean isFull();
    void print();
}
