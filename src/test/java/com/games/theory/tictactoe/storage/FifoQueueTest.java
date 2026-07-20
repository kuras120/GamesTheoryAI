package com.games.theory.tictactoe.storage;

import com.games.theory.tictactoe.model.GameCell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FifoQueueTest {
  private static final int SIZE = 3;
  private final IFifoQueue fifoQueue = new FifoQueue(SIZE);

  @AfterEach
  void afterEach() {
    fifoQueue.clear();
  }

  @Test
  void reportsCapacity() {
    fifoQueue.addFirst(cell(0, 1, "Y"));
    fifoQueue.addFirst(cell(1, 0, "X"));
    assertFalse(fifoQueue.isFull());
    fifoQueue.addFirst(cell(0, 2, "X"));
    assertTrue(fifoQueue.isFull());
  }

  @Test
  void ignoresDifferentMarks() {
    fifoQueue.addFirst(cell(0, 1, "Y"));
    fifoQueue.addFirst(cell(1, 0, "X"));
    fifoQueue.addFirst(cell(0, 2, "X"));
    assertNull(fifoQueue.findNewWinningSequence());
  }

  @Test
  void returnsCoordinatesForANewMatchOnlyOnce() {
    fifoQueue.addFirst(cell(0, 1, "X"));
    fifoQueue.addFirst(cell(0, 2, "X"));
    fifoQueue.addFirst(cell(0, 3, "X"));

    assertNotNull(fifoQueue.findNewWinningSequence());
    assertNull(fifoQueue.findNewWinningSequence());
  }

  private GameCell cell(int column, int row, String mark) {
    GameCell cell = new GameCell(column, row);
    cell.place(mark);
    return cell;
  }
}
