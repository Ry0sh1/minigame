function SPAWN(message){
    const content = JSON.parse(message.content);
    if (message.player === username){
        player.x = parseInt(content.x);
        player.y = parseInt(content.y);
        player.angle = 0;
        player.weapon = getWeaponFromString(content.weapon);
        player.hp = 100;
        player.alive = true;
        camera = new Camera(0,0, canvas.width, canvas.height);
        document.getElementById('hp').innerText = player.hp;
        document.getElementById('change-weapon-button').classList.add('hidden');
        document.getElementById('change-weapon').classList.add('hidden');
        document.getElementById('death-info').classList.add('hidden');
        document.getElementById('game-main').classList.remove('hidden');
        document.getElementById('change-weapon-button').classList.add('hidden');
        document.getElementById('change-weapon').classList.add('hidden');
    }else {
        const p = players.get(message.player);
        p.alive = true;
        p.weapon = content.weapon;
        p.x = content.x;
        p.y = content.y;
        p.angle = 0;
    }
}