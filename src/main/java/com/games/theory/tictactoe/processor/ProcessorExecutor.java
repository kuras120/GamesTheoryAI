package com.games.theory.tictactoe.processor;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProcessorExecutor {
  private final List<Processor> processors;

  public ProcessorExecutor() {
    processors = new LinkedList<>();
  }

  public ProcessorExecutor add(Processor processor) {
    processors.add(processor);
    return this;
  }

  public ProcessorExecutor execute(List<Node> tableList) {
    for (Node node : tableList) {
      if (node instanceof StackPane stackPane) {
        processors.forEach(processor -> processor.process(stackPane));
      }
    }
    return this;
  }

  public Map<String, Integer> collect() {
    Map<String, Integer> sumRoundPoints = processors.stream()
        .map(Processor::getPoints)
        .flatMap(m -> m.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    processors.forEach(Processor::reset);
    return sumRoundPoints;
  }
}
