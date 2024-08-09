function JOIN(message){
    if (message.player === username){
        const g = JSON.parse(message.content);
        minusSeconds(g.time);
        window.setInterval(() => {gameSec()}, 1000)
        fetch("/get-all-player/" + code)
            .then(response => response.json())
            .then(data => {
                for (let i = 0; i < data.length; i++){
                    if (data[i].username !== username){
                        players.set(data[i].username, new Player(parseInt(data[i].x),parseInt(data[i].y),data[i].username, data[i].angle, getWeaponFromString(data[i].weapon)));
                    }
                }
                player = new Player(-1000, -1000, username, 0, rifle);
            })
    }
}

function minusSeconds(seconds){
    let timeArr = document.getElementById("game-timer").innerText.split(":");
    let timeInSeconds = parseInt(timeArr[0])*60 + parseInt(timeArr[1]);
    let min = Math.floor((timeInSeconds - seconds) / 60);
    let sec = (timeInSeconds - seconds) % 60;
    if (sec < 10){
        sec = `0${sec}`;
    }
    document.getElementById("game-timer").innerText = `0${min}:${sec}`;
}
function gameSec(){
    minusSeconds(1);
    if (!alive && !firstSpawn){
        currentDeathTimer--;
        document.getElementById('respawn-timer').innerText = `${currentDeathTimer}`;
        if (currentDeathTimer <= 0){
            stompClient.send("/app/game.spawn/" + code,
                {},
                JSON.stringify({type: 'SPAWN', player: username,content: player.weapon.name, code: code})
            );
            document.getElementById('respawn-timer').classList.add('hidden');
        }
    }
    if (currentEvent != null){
        let currentTime = document.getElementById('current_event_timer').innerText;
        currentTime--;
        if (currentTime <= 0){
            stopEvent();
            document.getElementById('event_information').classList.add('hidden');
        }else {
            document.getElementById('current_event_timer').innerText = currentTime;
        }
    }
}