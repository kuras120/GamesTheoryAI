package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.storage.FifoQueue;
import com.games.theory.tictactoe.storage.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DiagonalProcessor implements Processor {
  private final IFifoQueue mainDiagonalQueue;
  private final IFifoQueue antiDiagonalQueue;
  private final Map<Integer, IFifoQueue> lesserDiagonalQueues;
  @Getter
  private final Map<String, Integer> points;

  private final int columns;
  private final int fifoSize;

  public DiagonalProcessor(int columns, int fifoSize) {
    mainDiagonalQueue = new FifoQueue(fifoSize);
    antiDiagonalQueue = new FifoQueue(fifoSize);
    lesserDiagonalQueues = new HashMap<>();
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    this.columns = columns;
    this.fifoSize = fifoSize;
  }

  @Override
  public void process(StackPane node) {
    Node userNode = (Node)node.getUserData();
    var col = userNode.getColIndex();
    var row = userNode.getRowIndex();
    if (col == row) {
      mainDiagonalQueue.addFirst(node);
    } else if (col == columns - row - 1) {
      antiDiagonalQueue.addFirst(node);
    } else {
      var diffX = Math.abs(col - (columns - 1) / 2);
      var diffY = Math.abs(row - (columns - 1) / 2);
      var diff = Math.min(diffX, diffY);
      lesserDiagonalQueues.computeIfAbsent(diff, k -> new FifoQueue(fifoSize));
      lesserDiagonalQueues.get(diff).addFirst(node);
    }
//    mainDiagonalQueue.print("MainDiagonal");
//    antiDiagonalQueue.print("AntiDiagonal");
//    lesserDiagonalQueues.forEach((k, v) -> v.print("LesserDiagonal-" + k));
  }

  @Override
  public void reset() {
    mainDiagonalQueue.clear();
    antiDiagonalQueue.clear();
    lesserDiagonalQueues.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
  }
}
