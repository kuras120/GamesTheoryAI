package com.games.theory.tictactoe.model;

import lombok.Data;

@Data
public class Node {
    private String markName;
    private int colIndex;
    private int rowIndex;
    private boolean checked;

    public Node(int colIndex, int rowIndex) {
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
        this.markName = "";
        this.checked = false;
    }
}
