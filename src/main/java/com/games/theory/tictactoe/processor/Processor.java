package com.games.theory.tictactoe.processor;

import javafx.scene.layout.StackPane;

import java.util.Map;

public interface Processor {
  void process(StackPane node);
  Map<String, Integer> getPoints();
  void reset();
}
