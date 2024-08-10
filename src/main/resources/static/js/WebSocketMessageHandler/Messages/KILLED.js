function KILLED(message){
    if (message.content === username){
        player.hp = 0;
        document.getElementById('hp').innerText = player.hp;
        alive = false;
        camera = null;
        document.getElementById('change-weapon-button').classList.remove('hidden');
        document.getElementById('respawn-timer').classList.remove('hidden');
        document.getElementById('death-text').classList.remove('hidden');
        document.getElementById('game-main').classList.add('hidden');
        currentDeathTimer = settings.respawnTimer;
    }
    if (players.has(message.content)) players.delete(message.content);
    const killerText = document.getElementById('player-card-'+message.player+"-kills")
    const deathText = document.getElementById('player-card-'+message.content+"-deaths")
    killerText.innerText = `${parseInt(killerText.innerText) + 1}`;
    deathText.innerText = `${parseInt(deathText.innerText) + 1}`
}