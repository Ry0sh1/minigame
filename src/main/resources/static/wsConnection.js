function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game/", onMessageReceived);

    stompClient.send("/app/game.join/",
        {},
        JSON.stringify({player: 'Ryoshi',content: 'TEST'})
    );
}

connect();

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    console.log("Message Received");
    if (message.gameCode === code){

    }
}
