package com.cloud.tictactoe.service;


import com.cloud.tictactoe.exception.GameNotFoundException;
import com.cloud.tictactoe.exception.InvalidGameException;
import com.cloud.tictactoe.exception.InvalidParamException;
import com.cloud.tictactoe.model.*;
import com.cloud.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {



    public Game createGame(Player player){

        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setGameStatus(GameStatus.NEW);
        game.setPlayer1(player);
        game.setCurrentMove(TicToe.X);
        GameStorage.getInstance().setGame(game);
        return game;

    }


    public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException{

        if (! GameStorage.getInstance().getGames().containsKey(gameId)){
            throw new InvalidParamException("Game does not exist!");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);

        if(game.getPlayer2() != null){
            throw new InvalidGameException("Game not in valid state to join. 2 players already!");
        }

        game.setPlayer2(player2);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;

    }

    public Game connectToRandomGame (Player player2) throws GameNotFoundException{
        Game game = GameStorage.getInstance().getGames()
                .values()
                .stream()
                .filter( it -> it.getGameStatus().equals(GameStatus.NEW))
                .findFirst()
                .orElseThrow( () -> new GameNotFoundException("No games available"));
        game.setPlayer2(player2);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }


    public Game gamePlay(GamePlay gamePlay) throws GameNotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new GameNotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getGameStatus().equals(GameStatus.FINISHED) || game.getGameStatus().equals(GameStatus.NEW)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        // is player's turn?
        if( game.getCurrentMove().equals(gamePlay.getType()))
            board[gamePlay.getX()][gamePlay.getY()] = gamePlay.getType().getValue();
        else
            return null;

        TicToe nextMove = gamePlay.getType().equals(TicToe.X)? TicToe.O : TicToe.X;
        game.setCurrentMove(nextMove);

        Boolean xWinner = checkWinner(game.getBoard(), TicToe.X);
        Boolean oWinner = checkWinner(game.getBoard(), TicToe.O);

        if (xWinner) {
            game.setWinner(TicToe.X);
            game.setGameStatus(GameStatus.FINISHED);
        } else if (oWinner) {
            game.setWinner(TicToe.O);
            game.setGameStatus(GameStatus.FINISHED);
        }

        if(game.getWinner() == null)
            if(checkTie(game.getBoard()))
                game.setGameStatus(GameStatus.FINISHED);


        GameStorage.getInstance().setGame(game);
        return game;
    }

    private Boolean checkTie(int [][] board){

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j] == 0)
                    return false;
            }
        }
        return true;

    }

    private Boolean checkWinner(int[][] board, TicToe ticToe) {
        int[] boardArray = new int[9];
        int counterIndex = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardArray[counterIndex] = board[i][j];
                counterIndex++;
            }
        }

        int[][] winCombinations = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}};

        for (int i = 0; i < winCombinations.length; i++) {
            int counter = 0;
            for (int j = 0; j < winCombinations[i].length; j++) {
                if (boardArray[winCombinations[i][j]] == ticToe.getValue()) {
                    counter++;
                    if (counter == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public Game endGame(String gameId) throws GameNotFoundException{

        if (!GameStorage.getInstance().getGames().containsKey(gameId)){
            throw new GameNotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);
        if (game.getGameStatus().equals(GameStatus.IN_PROGRESS) || game.getGameStatus().equals(GameStatus.NEW) ) {
            game.setGameStatus(GameStatus.GIVEN_UP);
        }
        else
            return null;

        GameStorage.getInstance().setGame(game);

        return game;

    }

}
