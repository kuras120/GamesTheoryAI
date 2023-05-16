package com.games.theory.tictactoe.storage;

import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.games.theory.tictactoe.model.Node;

import static org.junit.jupiter.api.Assertions.*;

class FifoQueueTest {
  private static final int SIZE = 3;
  private static final IFifoQueue FIFO_QUEUE = new FifoQueue(SIZE);

  @AfterEach
  void afterEach() {
    FIFO_QUEUE.clear();
  }

  @Test
  void fifoIsFullCheck() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    assertFalse(FIFO_QUEUE.isFull(), "Not full fifo after adding 2nd item");
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    assertTrue(FIFO_QUEUE.isFull(), "Full fifo after adding 3rd item");
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    assertTrue(FIFO_QUEUE.isFull(), "Full fifo after adding 4th item");
    FIFO_QUEUE.clear();
    assertFalse(FIFO_QUEUE.isFull(), "Empty fifo");
  }

  @Test
  void fifoElementsWithDifferentMarks() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    assertNull(FIFO_QUEUE.isAllEqual(), "Doesn't find a match on different marks");

  }

  @Test
  void fifoElementsInCorrectColumnOrder() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    assertNotNull(FIFO_QUEUE.isAllEqual(), "Finds a match");
  }

  @Test
  void fifoElementsNotInCorrectOrder() {
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(2, 0, "X"));
    assertThrows(IllegalStateException.class, FIFO_QUEUE::isAllEqual, "Throws an error");
  }

  private javafx.scene.Node createPane(int colIndex, int rowIndex, String mark) {
    StackPane pane = new StackPane();
    Node node = new Node(colIndex, rowIndex);
    node.setMarkName(mark);
    pane.setUserData(node);
    return pane;
  }
}
