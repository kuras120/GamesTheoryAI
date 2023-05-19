package com.games.theory.tictactoe.processor;

import com.google.common.io.Resources;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorExecutorTest {
    private final ProcessorExecutor processorExecutor = new ProcessorExecutor();

    @ParameterizedTest
    @MethodSource("cases")
    void executeAndCollect(List<Node> nodes, Map<String, Integer> expectedPoints, String points) {
        Map<String, Integer> actualPoints = processorExecutor
                .add(new RowProcessor(3))
                .add(new ColumnProcessor(3))
                .add(new DiagonalProcessor(3))
                .execute(nodes)
                .collect();
        assertEquals(expectedPoints, actualPoints, "Expect " + points + " for current map");
    }

    private static Stream<Arguments> cases() throws IOException {
        return Stream.of(
                Arguments.of(readMap("map1"), Map.of("X", 4, "O", 0), "4:0"),
                Arguments.of(readMap("map2"), Map.of("X", 2, "O", 2), "2:2")
        );
    }

    private static List<Node> readMap(String mapName) throws IOException {
        List<Node> nodes = new LinkedList<>();
        List<String> text = Resources.readLines(Resources.getResource(mapName), StandardCharsets.UTF_8);
        for (int i = 0; i < text.size(); i++) {
            for (int j = 0; j < text.get(i).length(); j++) {
                nodes.add(createNode(j, i, String.valueOf(text.get(i).charAt(j))));
            }
        }
        return nodes;
    }

    private static Node createNode(int x, int y, String player) {
        Node node = new StackPane();
        var userNode = new com.games.theory.tictactoe.model.Node(x, y);
        userNode.setMarkName(player);
        node.setUserData(userNode);
        return node;
    }
}
