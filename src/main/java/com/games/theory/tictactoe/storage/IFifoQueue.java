package com.games.theory.tictactoe.storage;

import com.games.theory.tictactoe.model.GameCell;
import com.games.theory.tictactoe.model.WinningSequence;

public interface IFifoQueue {
    void addFirst(GameCell cell);
    WinningSequence findNewWinningSequence();
    void clear();
    boolean isFull();
}
