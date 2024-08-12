function KILLED(message){
    if (message.content === username){
        player.hp = 0;
        document.getElementById('hp').innerText = player.hp;
        player.alive = false;
        camera = null;
        document.getElementById('change-weapon-button').classList.remove('hidden');
        document.getElementById('death-info').classList.remove('hidden');
        document.getElementById('game-main').classList.add('hidden');
        currentDeathTimer = settings.respawnTimer;
    }
    players.get(message.player).killCounter += 1;
    const deathPlayer = players.get(message.content);
    deathPlayer.deathCounter += 1;
    document.getElementById('player-card-'+message.player+"-kills").innerText = `${players.get(message.player).killCounter}`;
    document.getElementById('player-card-'+message.content+"-deaths").innerText = `${deathPlayer.deathCounter}`;
    deathPlayer.alive = false;
}