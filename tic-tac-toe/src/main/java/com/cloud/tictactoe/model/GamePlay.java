package com.cloud.tictactoe.model;


import lombok.Data;

@Data
public class GamePlay {

    private TicToe type;
    private Integer x;
    private Integer y;
    private String gameId;

}
