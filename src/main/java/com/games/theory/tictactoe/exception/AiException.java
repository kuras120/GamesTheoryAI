package com.games.theory.tictactoe.exception;

public class AiException extends RuntimeException {
  public AiException(String message) {
    super(message);
  }

  public AiException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
