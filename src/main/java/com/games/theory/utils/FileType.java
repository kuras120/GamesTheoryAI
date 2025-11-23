package com.games.theory.utils;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
    PIP("pip"),
    PYTHON("python"),
    GAMES_THEORY_INIT("games-theory-init"),
    GAMES_THEORY("games-theory");

    private final String command;
}
