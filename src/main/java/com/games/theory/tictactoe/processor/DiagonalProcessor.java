package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.utils.FifoQueue;
import com.games.theory.utils.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiagonalProcessor implements Processor {
  private static final String PATTERN_LEFT = "diagonal-to-left";
  private static final String PATTERN_RIGHT = "diagonal-to-right";
  private final Map<Integer, IFifoQueue> fifoQueuesLeft;
  private final Map<Integer, IFifoQueue> fifoQueuesRight;
  @Getter
  private final Map<String, Integer> points;

  public DiagonalProcessor(int columns, int fifoSize) {
    fifoQueuesLeft = IntStream.rangeClosed(-columns + 2, columns - 2).boxed()
        .collect(Collectors.toMap(Function.identity(), n -> new FifoQueue(fifoSize, PATTERN_LEFT)));
    fifoQueuesRight = IntStream.rangeClosed(-columns + 2, columns - 2).boxed()
        .collect(Collectors.toMap(Function.identity(), n -> new FifoQueue(fifoSize, PATTERN_RIGHT)));
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
  }

  @Override
  public void process(StackPane node) {
    Node userNode = (Node)node.getUserData();
    int index = userNode.getRowIndex() - userNode.getColIndex();
  }

  @Override
  public void reset() {
    fifoQueuesLeft.values().forEach(IFifoQueue::clear);
    fifoQueuesRight.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
  }
}
