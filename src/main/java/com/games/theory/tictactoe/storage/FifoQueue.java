package com.games.theory.tictactoe.storage;

import java.util.LinkedList;

import com.games.theory.tictactoe.model.GameCell;
import com.games.theory.tictactoe.model.WinningSequence;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FifoQueue extends LinkedList<GameCell> implements IFifoQueue {
    private final int limit;

    @Override
    public void addFirst(GameCell cell) {
        super.addFirst(cell);
        while (size() > limit) {
            super.removeLast();
        }
    }

    @Override
    public WinningSequence findNewWinningSequence() {
        var firstCell = getFirst();
        var firstMark = firstCell.markName();
        int freeNodeChecker = 0;
        if (!firstMark.isEmpty()) {
            for (GameCell cell : this) {
                if (!cell.markName().equals(firstMark)) return null;
                if (!cell.checked()) freeNodeChecker++;
            }
            if (freeNodeChecker == 0) {
                return null;
            }
            forEach(GameCell::markChecked);
            return new WinningSequence(
                firstMark,
                stream().map(GameCell::coordinate).toList()
            );
        }
        return null;
    }

    @Override
    public boolean isFull() {
        return super.size() >= this.limit;
    }
}
