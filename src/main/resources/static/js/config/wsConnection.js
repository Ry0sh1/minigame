function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game/" + code, onMessageReceived);

    stompClient.send("/app/game.join/" + code,
        {},
        JSON.stringify({player: username,content: 'Joined',type: 'JOIN', code: code})
    );
}

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    if (message.type === 'USE_POWERUP'){
        USE_POWERUP(message);
    }
    if (message.type === 'END_GAME'){
        END_GAME(message);
    }
    if (message.type === 'GAME_STATE') {
        GAME_STATE(message.content);
    }
    if (message.type === 'TIMER'){
        TIMER(message);
    }
    if (message.type === 'EVENT'){
        EVENT(message);
    }
    if (message.type === 'STOP_EVENT'){
        STOP_EVENT(message);
    }
    if (message.type === 'TOWER')  {
        TOWER(message);
    }
}