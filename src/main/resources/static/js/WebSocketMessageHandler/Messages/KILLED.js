function KILLED(message){
    if (message.content === username){
        player.hp = 0;
        document.getElementById('hp').innerText = player.hp;
        alive = false;
        camera = null;
        document.getElementById('change-weapon-button').classList.remove('hidden');
        document.getElementById('respawn-timer').classList.remove('hidden');
        currentDeathTimer = settings.respawnTimer;
    }
    if (players.has(message.content)) players.delete(message.content);

}