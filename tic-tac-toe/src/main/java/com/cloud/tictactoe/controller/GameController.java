package com.cloud.tictactoe.controller;


import com.cloud.tictactoe.controller.dto.ConnectRequest;
import com.cloud.tictactoe.controller.dto.EndRequest;
import com.cloud.tictactoe.exception.GameNotFoundException;
import com.cloud.tictactoe.exception.InvalidGameException;
import com.cloud.tictactoe.exception.InvalidParamException;
import com.cloud.tictactoe.model.Game;
import com.cloud.tictactoe.model.GamePlay;
import com.cloud.tictactoe.model.Player;
import com.cloud.tictactoe.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {


    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping("/start")
    public ResponseEntity<Game> start( @RequestBody Player player){

        log.info("start game request {}", player);
        return ResponseEntity.ok(gameService.createGame(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        Game game = gameService.connectToGame(request.getPlayer(), request.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + request.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws GameNotFoundException {
        log.info("connect random {}", player);
        Game game = gameService.connectToRandomGame(player);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws GameNotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        try{
            Game game = gameService.gamePlay(request);
            if(game == null)
                return ResponseEntity.badRequest().build();
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
            return ResponseEntity.ok(game);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/end")
    public ResponseEntity<Game> end(@RequestBody EndRequest request) {
        log.info("end request: {}", request);
        try{
            Game game = gameService.endGame(request.getGameId());

            if(game == null)
                return ResponseEntity.badRequest().build();

            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
            return ResponseEntity.ok(game);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }



}
