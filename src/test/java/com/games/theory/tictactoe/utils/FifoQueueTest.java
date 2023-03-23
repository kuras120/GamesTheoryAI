package com.games.theory.tictactoe.utils;

import com.games.theory.utils.FifoQueue;
import com.games.theory.utils.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.games.theory.tictactoe.model.Node;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FifoQueueTest {
  private static final int SIZE = 3;
  private static final String PATTERN = "test";
  private static final IFifoQueue FIFO_QUEUE = new FifoQueue(SIZE, PATTERN);

  @AfterEach
  void tearDown() {
    FIFO_QUEUE.clear();
  }

  @Test
  void addFirstTwoElements() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    assertFalse(FIFO_QUEUE.isFull(), "Empty fifo after adding 2 items");

  }

  @Test
  void addFirstThreeElements() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    assertTrue(FIFO_QUEUE.isFull(), "Full fifo after adding 3 items");
  }

  @Test
  void addFirstFourElements() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    assertTrue(FIFO_QUEUE.isFull(), "Full fifo after adding 4 items");
  }

  @Test
  void isAllEqualDifferentMarks() {
    FIFO_QUEUE.addFirst(createPane(0, 1, "Y"));
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    assertNull(FIFO_QUEUE.isAllEqual("column"), "Doesn't find a match after different marks");

  }

  @Disabled("Disabled until fix will be applied")
  @Test
  void isAllEqualDifferentIndices() {
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    assertNull(FIFO_QUEUE.isAllEqual("column"), "Doesn't find a match after different indices");
  }

  @Test
  void isAllEqual() {
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 1, "X"));
    assertNotNull(FIFO_QUEUE.isAllEqual("column"), "Finds a match");
  }

  @Disabled("Not needed for now")
  @Test
  void clear() {
    FIFO_QUEUE.addFirst(createPane(1, 0, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 2, "X"));
    FIFO_QUEUE.addFirst(createPane(0, 3, "X"));
    FIFO_QUEUE.clear();
    assertFalse(FIFO_QUEUE.isFull(), "Clears fifo");
  }

  private javafx.scene.Node createPane(int colIndex, int rowIndex, String mark) {
    StackPane pane = new StackPane();
    Node node = new Node(colIndex, rowIndex);
    node.setMarkName(mark);
    pane.setUserData(node);
    return pane;
  }
}
