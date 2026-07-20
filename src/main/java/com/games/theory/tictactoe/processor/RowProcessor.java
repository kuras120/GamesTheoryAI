package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.GameCell;
import com.games.theory.tictactoe.model.WinningSequence;
import com.games.theory.tictactoe.storage.FifoQueue;
import com.games.theory.tictactoe.storage.IFifoQueue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowProcessor implements Processor {
  private final IFifoQueue fifoQueue;
  @Getter
  private final Map<String, Integer> points;
  @Getter
  private final List<WinningSequence> winningSequences;
  private Integer rowIndex;

  public RowProcessor(int fifoSize) {
    fifoQueue = new FifoQueue(fifoSize);
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    winningSequences = new ArrayList<>();
    rowIndex = null;
  }

  @Override
  public void process(GameCell cell) {
    if (!Integer.valueOf(cell.row()).equals(rowIndex)) fifoQueue.clear();
    fifoQueue.addFirst(cell);
    rowIndex = cell.row();
    if (fifoQueue.isFull()) {
      WinningSequence sequence = fifoQueue.findNewWinningSequence();
      if (sequence != null) {
        points.put(sequence.mark(), points.get(sequence.mark()) + 1);
        winningSequences.add(sequence);
      }
    }
  }

  @Override
  public void reset() {
    fifoQueue.clear();
    points.put("X", 0);
    points.put("O", 0);
    winningSequences.clear();
    rowIndex = null;
  }
}
