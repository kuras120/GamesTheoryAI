package tictactoe.util;

import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tictactoe.model.Node;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FifoQueueTest {

  private static final int SIZE = 3;
  private static IFifoQueue fifoQueue;

  @BeforeEach
  void setUp() {
    fifoQueue = new FifoQueue(SIZE);
  }

  @AfterEach
  void tearDown() {
    assertTrue(fifoQueue.isFull());
    fifoQueue.clear();
    assertFalse(fifoQueue.isFull());
  }

  @Test
  void addFirst() {
    fifoQueue.addFirst(createPane(0, 1, "Y"));
    fifoQueue.addFirst(createPane(1, 0, "X"));
    assertFalse(fifoQueue.isFull());
    fifoQueue.addFirst(createPane(0, 2, "X"));
    assertTrue(fifoQueue.isFull());
    fifoQueue.addFirst(createPane(0, 3, "X"));
    assertTrue(fifoQueue.isFull());
  }

  @Test
  void isAllEqual() {
    fifoQueue.addFirst(createPane(0, 1, "Y"));
    fifoQueue.addFirst(createPane(1, 0, "X"));
    fifoQueue.addFirst(createPane(0, 2, "X"));
    assertNull(fifoQueue.isAllEqual("column"));
    fifoQueue.addFirst(createPane(0, 3, "X"));
//    assertNull(fifoQueue.isAllEqual("column"));
    fifoQueue.addFirst(createPane(0, 0, "X"));
    assertNotNull(fifoQueue.isAllEqual("column"));
  }

  private javafx.scene.Node createPane(int colIndex, int rowIndex, String mark) {
    StackPane pane = new StackPane();
    Node node = new Node(colIndex, rowIndex);
    node.setMarkName(mark);
    pane.setUserData(node);
    return pane;
  }
}
