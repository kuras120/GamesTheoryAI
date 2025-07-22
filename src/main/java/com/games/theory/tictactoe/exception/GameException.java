package com.games.theory.tictactoe.exception;

public class GameException extends RuntimeException {
  public GameException(String message) {
    super(message);
  }

  public GameException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
