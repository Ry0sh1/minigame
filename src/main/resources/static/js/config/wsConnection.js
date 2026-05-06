function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect(
        {
            username: username,
            code: code
        }, onConnected, onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game/" + code, onMessageReceived);
}

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    if (message.type === 'TIMER'){
        TIMER(message);
    }
    if (message.type === 'END_GAME'){
        END_GAME(message);
    }
    if (message.type === 'GAME_STATE') {
        GAME_STATE(message.content);
    }
}