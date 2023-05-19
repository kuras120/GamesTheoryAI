package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.storage.FifoQueue;
import com.games.theory.tictactoe.storage.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ColumnProcessor implements Processor {
  private final Map<Integer, IFifoQueue> fifoQueues;
  @Getter
  private final Map<String, Integer> points;

  private final int fifoSize;

  public ColumnProcessor(int fifoSize) {
    fifoQueues = new HashMap<>();
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    this.fifoSize = fifoSize;
  }

  @Override
  public void process(StackPane node) {
    var fifo = fifoQueues.computeIfAbsent(((Node)node.getUserData()).getColIndex(), k -> new FifoQueue(fifoSize));
    fifo.addFirst(node);
    if (fifo.isFull()) {
      String won = fifo.isAllEqual();
      if (won != null) {
        points.put(won, points.get(won) + 1);
      }
    }
  }

  @Override
  public void reset() {
    fifoQueues.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
  }
}
