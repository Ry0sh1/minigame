
const originalConsoleLog = console.log;

console.log = function(message) {
    if (typeof message === 'string' && (message.includes('destination:/start-game/game/') || message.includes('destination:/app/'))) {
        return;
    }
    originalConsoleLog.apply(console, arguments);
};


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
    if (message.type === 'POSITION'){
        POSITION(message);
    }
    if (message.type === 'JOIN'){
        JOIN(message);
    }
    if (message.type === 'LEFT'){
        LEFT(message);
    }
    if (message.type === 'SHOOT'){
        SHOOT(message);
    }
    if (message.type === 'DELETE_BULLET'){
        DELETE_BULLET(message);
    }
    if (message.type === 'SPAWN'){
        SPAWN(message);
    }
    if (message.type === 'PLAYER_HIT'){
        PLAYER_HIT(message);
    }
    if (message.type === 'KILLED'){
        KILLED(message);
    }
    if (message.type === 'HEAL'){
        HEAL(message);
    }
    if (message.type === 'REACTIVATE_HEAL'){
        REACTIVATE_HEAL(message);
    }
    if (message.type === 'EVENT'){
        EVENT(message);
    }
    if (message.type === 'END_GAME'){
        END_GAME(message);
    }
    if (message.type === 'VIEW_ANGLE'){
        VIEW_ANGLE(message);
    }
    if (message.type === 'TIMER'){
        TIMER(message);
    }
    if (message.type === 'STOP_EVENT'){
        STOP_EVENT(message);
    }
    if (message.type === 'SPAWN_POWERUP'){
        SPAWN_POWERUP(message);
    }
}