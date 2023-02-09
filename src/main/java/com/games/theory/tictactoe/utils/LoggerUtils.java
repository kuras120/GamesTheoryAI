package com.games.theory.tictactoe.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class LoggerUtils {

  public static void processLog(Process process) throws IOException {
    String stdInput = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
    String stdError = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
    // Read the output from the command
    log.info("Here is the standard output of the command:\n{}", stdInput);
    // Read any errors from the attempted command
    log.debug("Here is the standard error of the command (if any):\n{}", stdError);
  }
}
