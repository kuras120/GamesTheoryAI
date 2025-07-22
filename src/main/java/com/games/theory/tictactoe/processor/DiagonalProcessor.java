package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.storage.FifoQueue;
import com.games.theory.tictactoe.storage.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DiagonalProcessor implements Processor {
  private final Map<Integer, IFifoQueue> differenceDiagonalQueues;
  private final Map<Integer, IFifoQueue> sumDiagonalQueues;
  @Getter
  private final Map<String, Integer> points;

  private final int fifoSize;

  public DiagonalProcessor(int fifoSize) {
    differenceDiagonalQueues = new HashMap<>();
    sumDiagonalQueues = new HashMap<>();
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    this.fifoSize = fifoSize;
  }

  @Override
  public void process(StackPane node) {
    Node userNode = (Node)node.getUserData();
    var diff = userNode.getColIndex() - userNode.getRowIndex();
    var sum = userNode.getColIndex() + userNode.getRowIndex();
    processQueues(differenceDiagonalQueues, diff, node);
    processQueues(sumDiagonalQueues, sum, node);
  }

  @Override
  public void reset() {
    differenceDiagonalQueues.values().forEach(IFifoQueue::clear);
    sumDiagonalQueues.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
  }

  private void processQueues(Map<Integer, IFifoQueue> fifoQueues, int key, StackPane node) {
    fifoQueues.computeIfAbsent(key, k -> new FifoQueue(fifoSize));
    var diffQue = fifoQueues.get(key);
    diffQue.addFirst(node);
    if (diffQue.isFull()) {
      String won = diffQue.isAllEqual();
      if (won != null) {
        points.put(won, points.get(won) + 1);
      }
    }
  }
}
