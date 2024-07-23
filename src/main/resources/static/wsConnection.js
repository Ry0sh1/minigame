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
        JSON.stringify({player: player.username,content: 'Joined'})
    );
}

function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    if (message.type === 'POS'){
        let pos = message.content.split(',');

        player.x = parseInt(pos[0]);
        player.y = parseInt(pos[1]);
    }
}

document.getElementById('join-button').addEventListener('click', () => {
    player.username = document.getElementById('username').value;
    document.querySelector('.user-input').classList.add('hidden');
    canvas.classList.remove('hidden');
    connect();
    requestAnimationFrame(gameLoop);
})
