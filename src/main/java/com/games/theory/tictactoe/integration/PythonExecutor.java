package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.utils.DataReaderUtils;
import com.games.theory.utils.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Slf4j
public class PythonExecutor {
    private Process process;

    public void installDependencies() {
        try {
            // TODO skip for tests
            process = new ProcessBuilder(List.of(
                DataReaderUtils.getScript("venv/Scripts/pip.exe").getPath(),
                "install",
                "-r",
                DataReaderUtils.getScript("games_theory/requirements.txt").getPath()
            )).start();
            if (process.waitFor(1, TimeUnit.MINUTES)) {
                LoggerUtils.processLog(process);
                log.info("AI env installation completed");
            } else {
                process.destroy();
                throw new TimeoutException("Time exceeded for AI env installation process");
            }
        } catch (Exception ex) {
            log.error("AI error {}", ex.getMessage());
            throw new AiException("AI error", ex);
        }
    }

    public void processState(String pointsX, String pointsO, String[][] aiMap) {
        try {
            List<String> command = Stream.concat(
                Stream.of(
                    DataReaderUtils.getScript("venv/Scripts/python.exe").getPath(),
                    DataReaderUtils.getScript("games_theory/process.py").getPath(),
                    pointsX,
                    pointsO
                ),
                Arrays.stream(aiMap).flatMap(Arrays::stream).map(mark -> mark == null ? "N" : mark)
            ).toList();
            log.debug(command.toString());
            process = new ProcessBuilder(command).start();
            LoggerUtils.processLog(process);
        } catch (Exception ex) {
            log.error("AI error {}", ex.getMessage());
        }
    }
}
