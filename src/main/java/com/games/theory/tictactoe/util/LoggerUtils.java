package com.games.theory.tictactoe.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class LoggerUtils {

  public static void processLog(Process process) throws IOException {
    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    // Read the output from the command
    log.debug("Here is the standard output of the command:\n");
    String s;
    while ((s = stdInput.readLine()) != null) {
      log.info(s);
    }
    // Read any errors from the attempted command
    log.debug("Here is the standard error of the command (if any):\n");
    while ((s = stdError.readLine()) != null) {
      log.debug(s);
    }
  }
}
