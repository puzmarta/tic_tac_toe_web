package com.cloud.tictactoe.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TicToe {

     O(1), X(2);

    private Integer value;
}
