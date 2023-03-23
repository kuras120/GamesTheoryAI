package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.utils.FifoQueue;
import com.games.theory.utils.IFifoQueue;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class RowProcessor implements Processor {
  private static final String PATTERN = "row";
  private final IFifoQueue fifoQueue;
  @Getter
  private final Map<String, Integer> points;
  private Integer rowIndex;

  public RowProcessor(int fifoSize) {
    fifoQueue = new FifoQueue(fifoSize, PATTERN);
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    rowIndex = null;
  }

  @Override
  public void process(StackPane node) {
    if (!((Integer) ((Node)node.getUserData()).getRowIndex()).equals(rowIndex)) fifoQueue.clear();
    fifoQueue.addFirst(node);
    fifoQueue.print();
    rowIndex = ((Node) node.getUserData()).getRowIndex();
    if (fifoQueue.isFull()) {
      String won = fifoQueue.isAllEqual(PATTERN);
      if (won != null) {
        points.put(won, points.get(won) + 1);
      }
    }
  }

  @Override
  public void reset() {
    fifoQueue.clear();
    points.put("X", 0);
    points.put("O", 0);
  }
}
