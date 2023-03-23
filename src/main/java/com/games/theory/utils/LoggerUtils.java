package com.games.theory.utils;

import com.games.theory.tictactoe.exception.AiException;
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
    // TODO Remove after pip update
//    if (!stdError.isEmpty()) {
//      throw new AiException("Error output from AI script must be empty");
//    }
  }
}
