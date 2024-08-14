function END_GAME(message){
    player.alive = false;
    if (players.has(message.content)){
        const winner = message.content.split(",");
        document.getElementById('player-winner').innerText = winner;
        document.getElementById('player-winner').classList.remove('hidden');
        document.getElementById('game-main').classList.add('hidden');
        document.getElementById('change-weapon-info').classList.add('hidden');
        document.getElementById('change-weapon-button').classList.add('hidden');
        document.getElementById('death-info').classList.add('hidden');
    }
}