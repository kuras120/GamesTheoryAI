package com.games.theory;

public class GUI {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("tictactoe")) com.games.theory.tictactoe.Main.main(args);
        else com.games.theory.chess.Main.main(args);
    }
}
