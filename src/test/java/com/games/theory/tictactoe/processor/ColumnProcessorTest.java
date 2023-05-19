package com.games.theory.tictactoe.processor;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnProcessorTest {
    private final ColumnProcessor columnProcessor = new ColumnProcessor(3);

    @AfterEach
    void afterEach() {
        columnProcessor.reset();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void basicTest(List<StackPane> nodeList, Map<String, Integer> expectedPoints, String match) {
        nodeList.forEach(columnProcessor::process);
        assertEquals(columnProcessor.getPoints(), expectedPoints, "Expect " + match + " for current map");
    }

    private static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of(
                List.of(
                    createNode(0, 0, "X"),
                    createNode(0, 1, "X"),
                    createNode(0, 2, "X")
                ),
                Map.of("X", 1, "O", 0),
                "match"

            ),
            Arguments.of(
                List.of(
                    createNode(0, 0, "X"),
                    createNode(1, 0, "X"),
                    createNode(0, 2, "X")
                ),
                Map.of("X", 0, "O", 0),
                "no match"
            )
        );
    }

    private static Node createNode(int x, int y, String player) {
        Node node = new StackPane();
        var userNode = new com.games.theory.tictactoe.model.Node(x, y);
        userNode.setMarkName(player);
        node.setUserData(userNode);
        return node;
    }
}
