package com.cloud.tictactoe.controller.dto;

import com.cloud.tictactoe.model.Player;
import lombok.Data;


@Data
public class ConnectRequest {

    private Player player;
    private String gameId;
}
