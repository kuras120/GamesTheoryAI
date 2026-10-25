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

public class DiagonalProcessor implements Processor {
  private final Map<Integer, IFifoQueue> differenceDiagonalQueues;
  private final Map<Integer, IFifoQueue> sumDiagonalQueues;
  @Getter
  private final Map<String, Integer> points;
  @Getter
  private final List<WinningSequence> winningSequences;

  private final int fifoSize;

  public DiagonalProcessor(int fifoSize) {
    differenceDiagonalQueues = new HashMap<>();
    sumDiagonalQueues = new HashMap<>();
    points = new HashMap<>();
    points.put("X", 0);
    points.put("O", 0);
    winningSequences = new ArrayList<>();
    this.fifoSize = fifoSize;
  }

  @Override
  public void process(GameCell cell) {
    var diff = cell.column() - cell.row();
    var sum = cell.column() + cell.row();
    processQueues(differenceDiagonalQueues, diff, cell);
    processQueues(sumDiagonalQueues, sum, cell);
  }

  @Override
  public void reset() {
    differenceDiagonalQueues.values().forEach(IFifoQueue::clear);
    sumDiagonalQueues.values().forEach(IFifoQueue::clear);
    points.put("X", 0);
    points.put("O", 0);
    winningSequences.clear();
  }

  private void processQueues(Map<Integer, IFifoQueue> fifoQueues, int key, GameCell cell) {
    fifoQueues.computeIfAbsent(key, k -> new FifoQueue(fifoSize));
    var diffQue = fifoQueues.get(key);
    diffQue.addFirst(cell);
    if (diffQue.isFull()) {
      WinningSequence sequence = diffQue.findNewWinningSequence();
      if (sequence != null) {
        points.put(sequence.mark(), points.get(sequence.mark()) + 1);
        winningSequences.add(sequence);
      }
    }
  }
}
