package com.games.theory.utils;

import javafx.scene.Node;

public interface IFifoQueue {
    void addFirst(Node node);
    String isAllEqual();
    void clear();
    boolean isFull();
    void print();
}
