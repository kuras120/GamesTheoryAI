public class GUI {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("tictactoe")) tictactoe.Main.main(args);
        else chess.Main.main(args);
    }
}
