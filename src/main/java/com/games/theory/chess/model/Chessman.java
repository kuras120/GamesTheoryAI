package com.games.theory.chess.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Chessman {

    private String name;
    private String color;
    private String code;

    public Chessman(String name, String color, String code) {
        this.name = name;
        if ("W".equals(color)) this.color = "white";
        else if ("B".equals(color)) this.color = "black";
        this.code = code;
    }
}
