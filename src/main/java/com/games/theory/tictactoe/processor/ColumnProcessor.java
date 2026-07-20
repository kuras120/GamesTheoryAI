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

public class ColumnProcessor implements Processor {
  private final Map<Integer, IFifoQueue> fifoQueues;
  @Getter
  private final Map<String, Integer> points;
  @Getter
  private final List<WinningSequence> winningSequences;

  private final int fifoSize;

  public ColumnProcessor(int fifoSize) {
    fifoQueues = new HashMap<>();
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    winningSequences = new ArrayList<>();
    this.fifoSize = fifoSize;
  }

  @Override
  public void process(GameCell cell) {
    var fifo = fifoQueues.computeIfAbsent(cell.column(), k -> new FifoQueue(fifoSize));
    fifo.addFirst(cell);
    if (fifo.isFull()) {
      WinningSequence sequence = fifo.findNewWinningSequence();
      if (sequence != null) {
        points.put(sequence.mark(), points.get(sequence.mark()) + 1);
        winningSequences.add(sequence);
      }
    }
  }

  @Override
  public void reset() {
    fifoQueues.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
    winningSequences.clear();
  }
}
