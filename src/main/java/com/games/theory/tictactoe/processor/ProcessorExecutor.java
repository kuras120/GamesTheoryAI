package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.GameCell;
import com.games.theory.tictactoe.model.ScoringResult;
import com.games.theory.tictactoe.model.WinningSequence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessorExecutor {
  private final List<Processor> processors;

  public ProcessorExecutor() {
    processors = new LinkedList<>();
  }

  public ProcessorExecutor add(Processor processor) {
    processors.add(processor);
    return this;
  }

  public ProcessorExecutor execute(List<GameCell> cells) {
    for (GameCell cell : cells) {
      processors.forEach(processor -> processor.process(cell));
    }
    return this;
  }

  public ScoringResult collect() {
    Map<String, Integer> sumRoundPoints = processors.stream()
        .map(Processor::getPoints)
        .flatMap(m -> m.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    List<WinningSequence> winningSequences = processors.stream()
        .map(Processor::getWinningSequences)
        .collect(ArrayList::new, List::addAll, List::addAll);
    processors.forEach(Processor::reset);
    return new ScoringResult(sumRoundPoints, winningSequences);
  }

  public void reset() {
    processors.forEach(Processor::reset);
  }
}
