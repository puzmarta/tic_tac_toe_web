var turns = [["#", "#", "#"], ["#", "#", "#"], ["#", "#", "#"]];
var turn = "";
var gameOn = false;

function playerTurn(turn, id) {

    if (gameOn) {

        if(currentMove == playerType){

            var spotTaken = $("#" + id).text();
            if (spotTaken === "#") {
            makeAMove(playerType, id.split("_")[0], id.split("_")[1]);
            }
        }
        else{
            alert("It's not you turn")
        }

    }
    else{
        alert(" Game is not in progress")
    }
}

function makeAMove(type, xCoordinate, yCoordinate) {
    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "type": type,
            "x": xCoordinate,
            "y": yCoordinate,
            "gameId": gameId
        }),
        success: function (data) {
            gameOn = false;
            displayResponse(data);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function displayResponse(data) {
    let board = data.board;
    for (let i = 0; i < board.length; i++) {
        for (let j = 0; j < board[i].length; j++) {
            if (board[i][j] === 1) {
                turns[i][j] = 'O'
            } else if (board[i][j] === 2) {
                turns[i][j] = 'X';
            }
            let id = i + "_" + j;
            $("#" + id).text(turns[i][j]);
        }
    }
    currentMove = data.currentMove;
    let info_text = "";

    if(data.gameStatus == "IN_PROGRESS"){
        gameOn = true;
    }
    else{
        gameOn = false;
    }

    $('#turn_info').text("");

    switch(data.gameStatus){
        case "NEW":
            info_text = "Waiting for 2nd player";
            break;
        case "IN_PROGRESS":
            info_text = "You play as " + String(playerType);
            $('#turn_info').text("Now playing: " + data.currentMove);
            break;
        case "FINISHED":
            info_text = "Game is finished";
            break;
        case "GIVEN_UP":
            info_text = "Player gave up";
            gameOn = false;
            break;
    }
    $('#info').text(info_text);

    if (data.winner != null) {
        alert("Winner is " + data.winner);
        $('#turn_info').text("");
        gameOn = false;
    }
    else if (data.winner == null && data.gameStatus == "FINISHED"){
        alert("Game ended in tie");
        $('#turn_info').text("");
        gameOn = false;}

}

$(".tic").click(function () {
    var slot = $(this).attr('id');
    playerTurn(turn, slot);
});

function reset() {
    turns = [["#", "#", "#"], ["#", "#", "#"], ["#", "#", "#"]];
    $(".tic").text("#");
}

function resetBoard(){

         $.ajax({
                    url: url + "/game/end",
                    type: 'POST',
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify({
                        "gameId": gameId
                    }),
                    success: function (data) {
                        reset();

                    },
                    error: function (error) {
                        console.log(error);
                    }
                })

        reset();
        $('#turn_info').text("");
        $('#info').text("");


}