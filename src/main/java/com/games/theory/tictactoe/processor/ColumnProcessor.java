package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.storage.FifoQueue;
import com.games.theory.tictactoe.storage.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ColumnProcessor implements Processor {
  private static final String PATTERN = "column";
  private final Map<Integer, IFifoQueue> fifoQueues;
  @Getter
  private final Map<String, Integer> points;

  public ColumnProcessor(int columns, int fifoSize) {
    fifoQueues = IntStream.rangeClosed(0, columns).boxed()
        .collect(Collectors.toMap(Function.identity(), n -> new FifoQueue(fifoSize)));
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
  }

  @Override
  public void process(StackPane node) {
    var fifo = fifoQueues.get(((Node)node.getUserData()).getColIndex());
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
