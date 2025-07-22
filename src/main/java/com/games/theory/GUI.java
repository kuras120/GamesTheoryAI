package com.games.theory;

public class GUI {
    public static void main(String[] args) {
        if (args.length > 0 && "tictactoe".equals(args[0])) com.games.theory.tictactoe.Main.main(args);
        else com.games.theory.chess.Main.main(args);
    }
}
