package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.storage.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DiagonalProcessor implements Processor {
  private static final String PATTERN_LEFT = "diagonal-to-left";
  private static final String PATTERN_RIGHT = "diagonal-to-right";
  private IFifoQueue mainDiagonalQueue;
  private IFifoQueue antiDiagonalQueue;
  private Map<Integer, IFifoQueue> lesserDiagonalQueues;
  @Getter
  private final Map<String, Integer> points;

  public DiagonalProcessor(int columns, int fifoSize) {
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
    points.put("X", 0);
    points.put("O", 0);
  }
}
